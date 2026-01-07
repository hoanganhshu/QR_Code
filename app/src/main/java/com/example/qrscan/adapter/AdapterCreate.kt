package com.example.qrscan.adapter

import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.example.qrscan.R
import com.example.qrscan.databinding.ItemCreateBinding

class AdapterCreate : BaseAdapter<Map<String, Any?>>() {

    private val inputMap = mutableMapOf<String, String>()
    private var isEmpty : Boolean =false
    private val errorKeys = mutableSetOf<String>()
    private var recyclerView: RecyclerView? = null

    private val textWatchers =mutableMapOf<String , TextWatcher>()
    private var isPrefilling = false
    private var lastFocusedEditText: View? = null

    override fun getItemLayout(): Int = R.layout.item_create

    override fun setData(binding: ViewDataBinding, item: Map<String, Any?>, position: Int) {
        val mBinding = binding as ItemCreateBinding

        val key = item["key"]?.toString() ?: return
        val title = item["title"]?.toString() ?: ""
        val hint = item["subtitle"]?.toString() ?: ""
        Log.e(
            "BIND",
            "onBind key=$key position=$position " +
                    "focused=${mBinding.subtitle.isFocused} " +
                    "hash=${System.identityHashCode(mBinding.subtitle)}"
        )



        mBinding.subtitle.tag = key
        mBinding.root.tag = key

        mBinding.title.text = title
        mBinding.subtitle.hint = hint


        // Remove TextWatcher cũ
        textWatchers[key]?.let { oldWatcher ->
            mBinding.subtitle.removeTextChangedListener(oldWatcher)
        }
        
        val inputType = (item["inputType"] as? Int)
            ?: (InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES)
        if (!mBinding.subtitle.isFocused) {
            mBinding.subtitle.inputType = inputType
        }




        // Tạo TextWatcher mới TRƯỚC
        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // Chỉ lưu khi KHÔNG đang trong quá trình setText (isPrefilling = false)
                if (!isPrefilling) {
                    inputMap[key] = s?.toString() ?: ""
                    if (errorKeys.contains(key) && s?.toString()?.trim()?.isNotEmpty() == true) {
                        errorKeys.remove(key)
                        mBinding.error.visibility = View.GONE
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }
        
        textWatchers[key] = textWatcher

        // QUAN TRỌNG: setText() với isPrefilling = true để TextWatcher không lưu
        val wasPrefilling = isPrefilling
        isPrefilling = true

        val value = inputMap[key] ?: ""

        if (!mBinding.subtitle.isFocused) {
            mBinding.subtitle.setText(value)
        }
        isPrefilling = false

        mBinding.subtitle.addTextChangedListener(textWatcher)


        
        // Hiển thị error
        if (errorKeys.contains(key)) {
            mBinding.error.visibility = View.VISIBLE
        } else {
            mBinding.error.visibility = View.GONE
        }

        mBinding.subtitle.setOnFocusChangeListener { view, hasFocus ->
            Log.e(
                "FOCUS",
                "key=$key hasFocus=$hasFocus " +
                        "text='${mBinding.subtitle.text}' " +
                        "hash=${System.identityHashCode(mBinding.subtitle)}"
            )
            if (hasFocus) {
                lastFocusedEditText = view
            }
        }

        mBinding.subtitle.setOnClickListener {
            lastFocusedEditText = mBinding.subtitle
            mBinding.subtitle.requestFocus()
            clearError(key)
        }
        mBinding.subtitle.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_NEXT ||
                (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                val nextPosition = position + 1
                val rv = recyclerView ?: findRecyclerView(mBinding.root)

                if (rv != null && nextPosition < itemCount) {

                    val nextViewHolder = rv.findViewHolderForAdapterPosition(nextPosition)
                    if (nextViewHolder != null) {

                        val nextBinding = nextViewHolder.itemView
                        val nextEditText = nextBinding.findViewById<TextView>(R.id.subtitle)
                        nextEditText?.requestFocus()
                    } else {

                        rv.post {
                            rv.scrollToPosition(nextPosition)
                            rv.postDelayed({
                                val nextViewHolder = rv.findViewHolderForAdapterPosition(nextPosition)
                                nextViewHolder?.itemView?.findViewById<TextView>(R.id.subtitle)?.requestFocus()
                            }, 100)
                        }
                    }
                }
                true
            } else {
                false
            }
        }


        if (!inputMap.containsKey(key)) {
            inputMap[key] = ""
        }

    }
    private fun findRecyclerView(view: View): RecyclerView? {
        var parent = view.parent
        while (parent != null) {
            if (parent is RecyclerView) {
                recyclerView = parent
                return parent
            }
            parent = parent.parent
        }
        return null
    }
    fun setRecyclerView(rv: RecyclerView) {
        recyclerView = rv
    }
    fun requestFocusLastEditText() {
        lastFocusedEditText?.let { editText ->
            editText.post {
                if (editText.isAttachedToWindow && editText.isShown) {
                    editText.requestFocus()

                    val imm = editText.context.getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as? android.view.inputmethod.InputMethodManager
                    imm?.showSoftInput(editText, android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT)
                }
            }
        }
    }
    fun setError(key: String) {
        errorKeys.add(key)


    }


    fun clearError(key: String) {
        errorKeys.remove(key)

    }


    fun clearAllErrors() {
        errorKeys.clear()


    }
    fun clearInputMap() {
        inputMap.clear()
        errorKeys.clear()
        textWatchers.clear()
    }

    fun getUserInput(): Map<String, String> = inputMap.toMap()

    fun prefillInput(values: Map<String, String>) {
        Log.d("PREFILL", "START prefill values=$values")
        if (values.isEmpty()) return

        isPrefilling = true
        inputMap.clear()
        inputMap.putAll(values)
        notifyDataSetChanged()
        isPrefilling = false

        Log.d("PREFILL", "END prefill inputMap=$inputMap")
    }
    fun saveCurrentValuesFromViews() {
        recyclerView?.let { rv ->
            for (i in 0 until itemCount) {
                val viewHolder = rv.findViewHolderForAdapterPosition(i)
                viewHolder?.itemView?.let { itemView ->
                    val editText = itemView.findViewById<TextView>(R.id.subtitle)
                    val key = editText?.tag?.toString()
                    if (key != null && editText != null) {
                        // SỬA: Lưu tất cả giá trị, kể cả empty
                        inputMap[key] = editText.text?.toString() ?: ""
                    }
                }
            }
        }
    }

}


