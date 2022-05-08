package com.staminaleft.timefighter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    internal lateinit var tapMeButton: Button
    internal lateinit var scoreBoardTextView: TextView
    internal lateinit var timeLeftTextView: TextView

    internal var score = 0

    internal var isGameStarted = false
    internal lateinit var countDownTimer: CountDownTimer
    internal var initialTimerLeft: Long = 60000
    internal var decrementTimer: Long = 1000
    internal var currentTimeLeft: Long = 60000

    companion object {
        private val TAG = MainActivity::class.java.simpleName
        private const val SCORE_KEY = "SCORE_KEY"
        private const val TIME_LEFT_KEY = "TIME_LEFT_KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d(TAG, "onCreate has called. Score is: $score")

        tapMeButton = findViewById(R.id.tapMeButton)
        scoreBoardTextView = findViewById(R.id.scoreBoardTextView)
        timeLeftTextView = findViewById(R.id.timeLeftTextView)

        tapMeButton.setOnClickListener { view ->
            val bounce = AnimationUtils.loadAnimation(this, R.anim.bounce)
            view.startAnimation(bounce)

            incrementScore()
        }

        if (savedInstanceState != null) {
            score = savedInstanceState.getInt(SCORE_KEY)
            currentTimeLeft = savedInstanceState.getLong(TIME_LEFT_KEY)
            restoreGame()
        } else {
            resetGame()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(SCORE_KEY, score)
        outState.putLong(TIME_LEFT_KEY, currentTimeLeft)
        countDownTimer.cancel()

        Log.d(TAG, "onSaveInstanceState is called. Score: $score & Time Left: $currentTimeLeft")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy is called.")
    }

    private fun resetGame() {
        score = 0

        scoreBoardTextView.text = getString(R.string.yourScore, score)

        val initialTimeLeft = initialTimerLeft / 1000
        timeLeftTextView.text = getString(R.string.timeLeft, initialTimeLeft)
        
        countDownTimer = object : CountDownTimer(initialTimerLeft, decrementTimer) {
            override fun onTick(timeUntilFinished: Long) {
                currentTimeLeft = timeUntilFinished
                val timeLeft = timeUntilFinished / 1000
                timeLeftTextView.text = getString(R.string.timeLeft, timeLeft)
            }

            override fun onFinish() {
                finishGame()
            }
        }

        isGameStarted = false
    }

    private fun restoreGame() {
        scoreBoardTextView.text = getString(R.string.yourScore, score)

        val restoredTime = currentTimeLeft / 1000
        timeLeftTextView.text = getString(R.string.timeLeft, restoredTime)

        countDownTimer = object : CountDownTimer(currentTimeLeft, decrementTimer) {
            override fun onTick(timeUntilFinished: Long) {
                currentTimeLeft = timeUntilFinished
                val timeLeft = timeUntilFinished / 1000
                timeLeftTextView.text = getString(R.string.timeLeft, timeLeft)
            }

            override fun onFinish() {
                finishGame()
            }
        }

        countDownTimer.start()
        isGameStarted = true
    }

    private fun incrementScore() {
        if (!isGameStarted) {
            startGame()
        }
        score += 1
        val newScore = getString(R.string.yourScore, score)
        scoreBoardTextView.text = newScore

        val blink = AnimationUtils.loadAnimation(this, R.anim.blink)
        scoreBoardTextView.startAnimation(blink)
    }

    private fun startGame() {
        countDownTimer.start()
        isGameStarted = true
    }

    private fun finishGame() {
        Toast.makeText(this, getString(R.string.finalScore, score), Toast.LENGTH_LONG).show()
        resetGame()
    }
}