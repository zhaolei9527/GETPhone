package com.yinmeng

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.support.annotation.StyleRes
import android.view.KeyEvent

/**
 * com.sakuraphonebtc.View

 * @author 赵磊
 * *
 * @date 2018/3/27
 * * 功能描述：
 */
class BackDiaLog : Dialog {


    constructor(context: Context) : super(context) {
        val keylistener = DialogInterface.OnKeyListener { dialog, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK && event.repeatCount == 0) {
                try {
                    val context1 = context as Activity
                    context1.finish()
                    return@OnKeyListener true
                } catch (e: Exception) {
                    e.printStackTrace()
                    return@OnKeyListener false
                }

            } else {
                false
            }
        }

        setOnKeyListener(keylistener)
        setCancelable(false)
    }

    constructor(context: Context, @StyleRes themeResId: Int) : super(context, themeResId) {
        val keylistener = DialogInterface.OnKeyListener { dialog, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK && event.repeatCount == 0) {
                try {
                    val context1 = context as Activity
                    dismiss()
                    context1.finish()
                    return@OnKeyListener true
                } catch (e: Exception) {
                    e.printStackTrace()
                    return@OnKeyListener false
                }

            } else {
                false
            }
        }

        setOnKeyListener(keylistener)
        setCancelable(false)
    }

    protected constructor(context: Context, cancelable: Boolean, cancelListener: DialogInterface.OnCancelListener?) : super(context, cancelable, cancelListener) {
        val keylistener = DialogInterface.OnKeyListener { dialog, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK && event.repeatCount == 0) {
                try {
                    val context1 = context as Activity
                    context1.finish()
                    return@OnKeyListener true
                } catch (e: Exception) {
                    e.printStackTrace()
                    return@OnKeyListener false
                }

            } else {
                false
            }
        }

        setOnKeyListener(keylistener)
        setCancelable(false)
    }


}
