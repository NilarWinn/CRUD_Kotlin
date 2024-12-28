package com.example.bulletinboard.ui

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.os.bundleOf
import androidx.core.view.*
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.bulletinboard.MainApplication
import com.example.bulletinboard.R
import com.example.bulletinboard.data.MainRepository
import com.example.bulletinboard.data.Result
import com.example.bulletinboard.data.service.RetrofitService
import com.example.bulletinboard.databinding.ActivityHomeBinding
import com.example.bulletinboard.databinding.DrawerHeaderBinding
import com.example.bulletinboard.ui.custom.ProgressDialog
import com.example.bulletinboard.ui.post.*
import com.example.bulletinboard.ui.user.UserCreateActivity
import com.example.bulletinboard.ui.user.UserListActivity
import com.example.bulletinboard.ui.profile.ProfileActivity
import com.example.bulletinboard.ui.util.*
import com.example.bulletinboard.ui.util.ViewUtil.Companion.getCoroutineExceptionHandler
import com.example.bulletinboard.ui.util.ViewUtil.Companion.handleResult
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {

    private lateinit var repository: MainRepository
    private lateinit var binding: ActivityHomeBinding
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        repository = (application as MainApplication).mainRepository
        progressDialog = ProgressDialog(this)

        doOperations()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)
        return true
    }

    private var isSearchMode = false

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.search -> {
                isSearchMode = true
                invalidateOptionsMenu()
                return true
            }
            R.id.close -> {
                isSearchMode = false
                invalidateOptionsMenu()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        menu.findItem(R.id.search).isVisible = !isSearchMode
        menu.findItem(R.id.close).isVisible = isSearchMode
        binding.actionBar.layoutSearch.isVisible = isSearchMode
        return super.onPrepareOptionsMenu(menu)
    }

    private fun doOperations() {
        setTitle()
        setUpToolbar()
        setUpNavigationDrawer()
        setUpCsvFileChooser()
        setupFloatingButton()
        setUpSearchView()
    }

    private fun setTitle() {
        binding.actionBar.lblTitle.text = getString(R.string.title_post_list)
    }

    private fun setUpToolbar() {

        // To Inflate Menu for Searching
        setSupportActionBar(binding.actionBar.toolBar)

        // ActionBarDrawer
        val toggle = ActionBarDrawerToggle(
            this, binding.drawerLayout, binding.actionBar.toolBar,
            R.string.action_open, R.string.action_close
        )
        toggle.syncState()
    }

    private fun setUpNavigationDrawer() {

        // Navigation Header
        DrawerHeaderBinding.bind(binding.navigation.getHeaderView(0)).apply {

            val user = repository.loginData

            // Load Image
            if (user!!.profile != null) {
                Glide.with(this@HomeActivity)
                    .load("${RetrofitService.URL}${user.profile}")
                    .into(imgProfile)
            } else imgProfile.setImageResource(R.drawable.ic_user)

            // Bind User Info
            lblUserName.text = user.name
            lblUserRole.text = when (user.type) {
                0 -> "Admin"
                1 -> "User"
                else -> null
            }
        }

        // Navigation View
        binding.navigation.apply {
            inflateMenu(
                when (repository.loginData!!.type) {
                    0 -> R.menu.menu_drawer_admin
                    else -> R.menu.menu_drawer_user
                }
            )
            setNavigationItemSelectedListener {
                binding.actionBar.searchView.clearFocus()
                when (it.itemId) {
                    R.id.profile -> navigate(ProfileActivity::class.java)
                    R.id.user_add -> navigate(UserCreateActivity::class.java)
                    R.id.user_list -> navigate(UserListActivity::class.java)
                    R.id.post_add -> navigate(PostCreateActivity::class.java)
                    R.id.post_list -> navigate(PostListActivity::class.java)
                    R.id.user_post_list -> navigate(UserPostListActivity::class.java)
                    R.id.logout -> {
                        logout()
                        true
                    }
                    else -> false
                }
            }
        }
    }

    private fun navigate(className: Class<*>): Boolean {
        launchActivity(className)
        return true
    }

    private fun setUpCsvFileChooser() {
        val resultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode != Activity.RESULT_OK) return@registerForActivityResult

            val uri = it.data!!.data!!

            // Check File Type
            if (FileUtil.getExtension(applicationContext, uri) != "csv") {
                showToast(getString(R.string.error_file_not_supported))
                return@registerForActivityResult
            }

            val file = FileUtil.getFile(applicationContext, it.data!!.data)
            lifecycleScope.launch(getCoroutineExceptionHandler { progressDialog.hide() }) {
                progressDialog.show()
                when (repository.uploadCsv(file!!)) {
                    is Result.Success -> showToast(R.string.success_upload)
                    else -> showToast(R.string.error_unknown)
                }
                progressDialog.hide()
            }
        }

        val intent = Intent.createChooser(with(Intent()) {
            type = "text/*"
            setAction(Intent.ACTION_GET_CONTENT)
        }, "Choose File")

        binding.btnUpload.setOnClickListener { resultLauncher.launch(intent) }
    }

    private fun setupFloatingButton() {
        binding.apply {
            floatingBtnMain.setOnClickListener { toggleFloatingButtons() }
            btnDownload.setOnClickListener {
                downloadCsvFile()
                toggleFloatingButtons()
            }
            btnSearch.setOnClickListener {
                isSearchMode = true
                invalidateOptionsMenu()
                toggleFloatingButtons()
            }
            fabBackground.setOnClickListener {
                it.visibility = View.GONE
                toggleFloatingButtons()
            }
        }
    }

    private fun setUpSearchView() {
        binding.actionBar.searchView.apply {
            setOnQueryTextListener(object: SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(p0: String?): Boolean {
                    if (p0 == null) return true
                    clearFocus()
                    hideKeyboard()
                    goToSearchActivity(p0)
                    return true
                }

                override fun onQueryTextChange(p0: String?): Boolean { return false }
            })

            binding.actionBar.imgSearch.setOnClickListener {
                val query = this.query.toString().ifBlank { return@setOnClickListener }
                goToSearchActivity(query)
            }
        }
    }

    private fun logout() {
        lifecycleScope.launch(getCoroutineExceptionHandler { progressDialog.hide() }) {
            progressDialog.show()
            val result = repository.logout()
            if (result is Result.Success) {
                repository.clearLoginData()
                navigate(LoginActivity::class.java)
                finish()
            } else { showToast(result.toString()) }
            progressDialog.hide()
        }
    }

    private var isFloatingButtonsOn = false
    private fun toggleFloatingButtons() {
        isFloatingButtonsOn = !(isFloatingButtonsOn)
        binding.apply {
            groupDownload.isVisible = isFloatingButtonsOn
            groupSearch.isVisible = isFloatingButtonsOn
            groupUpload.isVisible = isFloatingButtonsOn
            fabBackground.visibility = if (isFloatingButtonsOn) View.VISIBLE else View.GONE
            floatingBtnMain.setImageResource(
                if (isFloatingButtonsOn) R.drawable.ic_xmark_solid
                else R.drawable.ic_plus_solid
            )
        }
    }

    private fun downloadCsvFile() {
        lifecycleScope.launch(getCoroutineExceptionHandler { progressDialog.hide() }) {
            progressDialog.show()
            handleResult(repository.downloadCsv()) {
                if (FileUtil.saveFile(it.toString()) != null) showToast(getString(R.string.success_download))
                else showToast(getString(R.string.error_download))
            }
            progressDialog.hide()
        }
    }

    private fun goToSearchActivity(query: String) {
        launchActivity(
            PostSearchListActivity::class.java, bundleOf("searchKey" to query)
        )
    }
}
