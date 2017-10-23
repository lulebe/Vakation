package de.lulebe.vakation.ui

import android.animation.Animator
import android.view.View
import de.lulebe.vakation.ui.ViewExt.fadeOut


object ViewExt {

    fun View.fadeIn() {
        this.animation?.cancel()
        this.alpha = 0F
        this.visibility = View.VISIBLE
        val anim = this.animate().alpha(1F)
                .setDuration(context.resources.getInteger(android.R.integer.config_shortAnimTime).toLong())
        anim.setListener(object: Animator.AnimatorListener {
            override fun onAnimationRepeat(p0: Animator?) {}
            override fun onAnimationCancel(p0: Animator?) {
                this@fadeIn.alpha = 1F
            }
            override fun onAnimationStart(p0: Animator?) {}
            override fun onAnimationEnd(p0: Animator?) {}
        })
        anim.start()

    }
    fun View.fadeOut() {
        this.animation?.cancel()
        val anim = this.animate().alpha(0F)
                .setDuration(context.resources.getInteger(android.R.integer.config_shortAnimTime).toLong())
        anim.setListener(object: Animator.AnimatorListener {
            override fun onAnimationRepeat(p0: Animator?) {}
            override fun onAnimationCancel(p0: Animator?) {
                this@fadeOut.visibility = View.GONE
                this@fadeOut.alpha = 1F
            }
            override fun onAnimationStart(p0: Animator?) {}
            override fun onAnimationEnd(p0: Animator?) {
                this@fadeOut.visibility = View.GONE
                this@fadeOut.alpha = 1F
            }
        })
        anim.start()
    }

    fun View.scaleUpIn() {
        this.animation?.cancel()
        this.scaleX = 0.6F
        this.scaleY = 0.6F
        this.alpha = 0F
        this.visibility = View.VISIBLE
        this.animate().alpha(1F).scaleX(1F).scaleY(1F)
                .setDuration(context.resources.getInteger(android.R.integer.config_shortAnimTime).toLong())
                .start()
    }
    fun View.scaleDownOut() {
        this.animation?.cancel()
        val anim = this.animate().alpha(0F).scaleX(0.6F).scaleY(0.6F)
                .setDuration(context.resources.getInteger(android.R.integer.config_shortAnimTime).toLong())
        anim.setListener(object: Animator.AnimatorListener {
            override fun onAnimationRepeat(p0: Animator?) {}
            override fun onAnimationCancel(p0: Animator?) {}
            override fun onAnimationStart(p0: Animator?) {}
            override fun onAnimationEnd(p0: Animator?) {
                this@scaleDownOut.visibility = View.GONE
            }
        })
        anim.start()
    }
}