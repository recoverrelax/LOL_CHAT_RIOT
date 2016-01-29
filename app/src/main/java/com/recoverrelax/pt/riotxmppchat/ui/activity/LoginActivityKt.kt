package com.recoverrelax.pt.riotxmppchat.ui.activity

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.app.ProgressDialog
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.Snackbar
import android.view.View
import android.widget.*
import com.jakewharton.rxbinding.widget.RxTextView
import com.recoverrelax.pt.riotxmppchat.EventHandling.Publish.OnConnectionFailurePublish
import com.recoverrelax.pt.riotxmppchat.EventHandling.Publish.OnLoginFailurePublish
import com.recoverrelax.pt.riotxmppchat.EventHandling.Publish.OnServiceBindedPublish
import com.recoverrelax.pt.riotxmppchat.EventHandling.Publish.OnSuccessLoginPublish
import com.recoverrelax.pt.riotxmppchat.MainApplication
import com.recoverrelax.pt.riotxmppchat.MyUtil.Kotlin.overridePendingTransitionBackAppDefault
import com.recoverrelax.pt.riotxmppchat.MyUtil.Kotlin.setVisible
import com.recoverrelax.pt.riotxmppchat.MyUtil.Kotlin.showSnackBar
import com.recoverrelax.pt.riotxmppchat.MyUtil.Kotlin.stringFromRes
import com.recoverrelax.pt.riotxmppchat.R
import com.recoverrelax.pt.riotxmppchat.Riot.Enum.RiotServer
import com.squareup.otto.Subscribe
import org.jetbrains.anko.*
import rx.Observable
import javax.inject.Inject

class LoginActivityKt: BaseActivity(){

    private val username by lazy{ find<EditText>(R.id.username) }
    private val password by lazy{ find<EditText>(R.id.password) }
    private val serverSpinner by lazy{ find<Spinner>(R.id.serverSpinner) }
    private val connect by lazy{ find<Button>(R.id.connect) }
    private val checkBox by lazy{ find<CheckBox>(R.id.checkBox) }
    private val lol_logo by lazy{ find<ImageView>(R.id.lol_logo) }
    private val login_bottom_layout by lazy{ find<LinearLayout>(R.id.login_bottom_layout) }
    private val login_main_layout by lazy{ find<LinearLayout>(R.id.login_main_layout) }

    @Inject lateinit var mainApplication: MainApplication

    private var progressDialog: ProgressDialog? = null
    private val serverSpinnerAdapter by lazy{
        ArrayAdapter<String>(this, R.layout.spinner_layout, R.id.server_textview, RiotServer.getServerList())
    }

    override fun getLayoutResources() = R.layout.login_activity
    override fun getToolbarTitle() = null
    override fun getToolbarColor() = null
    override fun getToolbarTitleColor() = null
    override fun getNavigationViewPosition() = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainApplication.getInstance().appComponent.inject(this)

        doLoginAnimations()
        configCredentialsInteraction()

        checkBox.isChecked = mDataStorage.saveLoginCredentials
        serverSpinner.adapter = serverSpinnerAdapter

        if (mDataStorage.saveLoginCredentials) {
            username.setText(mDataStorage.username)
            password.setText(mDataStorage.password)
            serverSpinner.setSelection(RiotServer.getServerPositionByName(mDataStorage.server))
        } else
            connect.isEnabled = false

        connect.onClick{
            progressDialog = ProgressDialog(this)

            progressDialog?.run{
                title = stringFromRes(R.string.activity_login_progress_dialog_title)
                setMessage(stringFromRes(R.string.activity_login_progress_dialog_message))
                progress = 0
                setCancelable(false)
                show()
            }

            mainApplication.startRiotXmppService(
                    serverSpinner.selectedItem as String,
                    username.text.toString(),
                    password.text.toString()
            )
        }
    }

    private fun configCredentialsInteraction() {
        val usernameTextWatcher = RxTextView.textChangeEvents(username)
        val passwordTextWatcher = RxTextView.textChangeEvents(password)

        Observable.combineLatest(usernameTextWatcher, passwordTextWatcher,
                { t1, t2 ->
                    t1.text().toString().trim().count() > 3 && t2.text().toString().trim().count() > 3
                }
        ).subscribe{ connect.enabled = it }
    }

    override fun onResume() {
        super.onResume()
        bus.register(this)
    }

    override fun onPause() {
        super.onPause()
        bus.unregister(this)
    }

    private fun doLoginAnimations() {
        /**
         * Set initial Title Scalling to 0.7f
         */
        lol_logo.scaleY = 0.7f
        lol_logo.scaleX = 0.7f

        /**
         * EXPERIMENTAL!
         */

        login_bottom_layout.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        login_main_layout.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        lol_logo.setLayerType(View.LAYER_TYPE_HARDWARE, null)


        val titleSlideUp = ObjectAnimator.ofFloat(lol_logo, "translationY", 1000f, -100f, 0f)
                            .setDuration(3000)

        val fadingBackground = ObjectAnimator.ofPropertyValuesHolder(login_main_layout.background, PropertyValuesHolder.ofInt("alpha", 0))
                .setDuration(4000)

        val scalingTitleX = ObjectAnimator.ofFloat(lol_logo, "scaleX", 0.7f, 0.8f, 0.9f, 1.0f)
        val scalingTitleY = ObjectAnimator.ofFloat(lol_logo, "scaleY", 0.7f, 0.8f, 0.9f, 1.0f)

        val credentialsFade = ObjectAnimator.ofFloat(login_bottom_layout, "alpha", 0.0f, 1.0f)
        credentialsFade.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animator: Animator) {
                login_bottom_layout.setVisible()
            }

            override fun onAnimationEnd(animator: Animator) { }
            override fun onAnimationCancel(animator: Animator) { }
            override fun onAnimationRepeat(animator: Animator) { }
        })

        val animatorSetSecondPart = AnimatorSet().apply{
            setDuration(1000)
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationEnd(animation: Animator) {
                    login_bottom_layout.setLayerType(View.LAYER_TYPE_NONE, null)
                    lol_logo.setLayerType(View.LAYER_TYPE_NONE, null)
                    login_main_layout.setLayerType(View.LAYER_TYPE_NONE, null)
                }
                override fun onAnimationStart(animation: Animator) { }
                override fun onAnimationCancel(animation: Animator) { }
                override fun onAnimationRepeat(animation: Animator) { }
            })
            playTogether(scalingTitleX, scalingTitleY, credentialsFade)
        }

        titleSlideUp.start()
        fadingBackground.start()

        Handler().postDelayed({ animatorSetSecondPart.start() }, 2000)
    }

    @Subscribe
    fun onSuccessLogin(event: OnSuccessLoginPublish) {

        with(mDataStorage){
            setSaveLoginCredentials(checkBox.isChecked)
            setUsername(username.text.toString())
            setPassword(password.text.toString())
            setServer(serverSpinner.selectedItem as String)
        }
        mainApplication.bindService()
    }

    @Subscribe
    fun onServiceBinded(event: OnServiceBindedPublish) {
        startActivity(intentFor<DashboardActivityKt>().singleTop().clearTop())
        overridePendingTransitionBackAppDefault()

        progressDialog?.dismiss()
        finish()
    }

    @Subscribe
    fun onConnectionFailure(event: OnConnectionFailurePublish) {
        progressDialog?.dismiss()
        showSnackBar(R.string.activity_connection_failed, Snackbar.LENGTH_LONG)
    }

    @Subscribe
    fun onLoginFailure(event: OnLoginFailurePublish) {
        progressDialog?.dismiss()
        showSnackBar(R.string.activity_login_failed, Snackbar.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        progressDialog?.dismiss()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        progressDialog?.dismiss()
        finish()
    }
}