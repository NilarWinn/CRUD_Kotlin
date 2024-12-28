package com.example.bulletinboard.ui.base.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import androidx.core.view.isVisible
import com.example.bulletinboard.R
import com.example.bulletinboard.ui.base.PagingAdapter
import com.example.bulletinboard.ui.util.hideKeyboard

abstract class BaseListAndSearchActivity<T>(adapter: PagingAdapter<T>): BaseListActivity<T>(adapter) {

    private var isSearchMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(binding.toolBar)
        setUpSearchView()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.search -> {
                setSearchMode(true)
                return true
            }
            R.id.close -> {
                setSearchMode(false)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        menu.findItem(R.id.search).isVisible = !isSearchMode
        menu.findItem(R.id.close).isVisible = isSearchMode
        binding.layoutSearch.isVisible = isSearchMode
        return super.onPrepareOptionsMenu(menu)
    }

    private fun setUpSearchView() {
        binding.searchView.apply {
            setOnQueryTextListener(object: SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(p0: String?): Boolean {
                    if (p0 == null) return true
                    clearFocus()
                    hideKeyboard()
                    doSearchOperation(p0)
                    return true
                }

                override fun onQueryTextChange(p0: String?): Boolean { return false }
            })

            binding.imgSearch.setOnClickListener {
                val query = this.query.toString().ifBlank { return@setOnClickListener }
                doSearchOperation(query)
            }
        }
    }

    private fun setSearchMode(isSearchModeOn: Boolean) {
        isSearchMode = isSearchModeOn
        invalidateOptionsMenu()
    }

    abstract fun doSearchOperation(query: String)
}