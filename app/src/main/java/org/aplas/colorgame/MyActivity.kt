package org.aplas.colorgame

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.ViewGroup
import android.widget.*
import java.lang.reflect.Array.get
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.random.Random

class MyActivity : AppCompatActivity() {

    var timer: TextView ?= null
    var clrText: TextView ?= null
    var scoreText: TextView ?= null
    var passwd: EditText ?= null
    var submit: Button ?= null
    var start: Button ?= null
    var accessbox: ViewGroup ?= null
    var colorbox: ViewGroup ?= null
    var buttonbox1: ViewGroup ?= null
    var buttonbox2: ViewGroup ?= null
    var scorebox: ViewGroup ?= null
    var progressbox: ViewGroup ?= null
    var progress: ProgressBar ?= null
    var isMinus: Switch? = null

    var countDown: CountDownTimer? = null
    val FORMAT: String = "%d:%d"

    var clrList: Array<String?>? = null
    var charList: HashMap<String,String> = HashMap<String,String>()

    var isStarted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_layout)

        timer = findViewById<View>(R.id.timerText) as TextView
        clrText = findViewById<View>(R.id.clrText) as TextView
        scoreText = findViewById<View>(R.id.scoreText) as TextView
        passwd = findViewById<View>(R.id.appCode) as EditText
        submit = findViewById<View>(R.id.submitBtn) as Button
        start = findViewById<View>(R.id.startBtn) as Button
        accessbox = findViewById<View>(R.id.accessBox) as ViewGroup
        colorbox = findViewById<View>(R.id.colorBox) as ViewGroup
        buttonbox1 = findViewById<View>(R.id.buttonBox1) as ViewGroup
        buttonbox2 = findViewById<View>(R.id.buttonBox2) as ViewGroup
        scorebox = findViewById<View>(R.id.scoreBox) as ViewGroup
        progressbox = findViewById<View>(R.id.progressBox) as ViewGroup
        progress = findViewById<View>(R.id.progressScore) as ProgressBar
        isMinus = findViewById<View>(R.id.isMinus) as Switch

        initTimer()
        initColorList()


    }

    private fun initTimer(){
        var milisInFuture: Long = (resources.getInteger(R.integer.maxtimer) * 1000).toLong()
        var countDownInterval: Long = 1
        countDown = object : CountDownTimer(milisInFuture, countDownInterval){
            override fun onTick(millisUntilFinished: Long) {
                timer!!.text = "" + String.format(FORMAT, TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)
                        - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes( millisUntilFinished)), TimeUnit.MILLISECONDS.toMillis(millisUntilFinished)
                        - TimeUnit.SECONDS.toMillis(TimeUnit.MILLISECONDS.toSeconds( millisUntilFinished)))
            }

            override fun onFinish() {
                wrongSubmit()
            }
        }
    }

    private fun initColorList(){
        clrList = resources.getStringArray(R.array.colorList)
        val temp: Array<String> = resources.getStringArray(R.array.charList)
        for (i in (clrList as Array<String>).indices){
            charList[(clrList as Array<String>)[i]] = temp[i]
        }
        if(isStarted){
            val clrTxt =(findViewById<View>(R.id.clrText) as TextView).text.toString()
            val lastNum = Arrays.asList(*clrList as Array<String>).indexOf(clrTxt)
            val colorIdx = getNewRandomInt(0, 5, lastNum)
            clrText!!.text = (clrList as Array<String>)[colorIdx]
            countDown!!.start()
        }
    }

    fun getNewRandomInt(min: Int, max: Int, except: Int):
            Int {
        val r = Random
        var found = false
        var number: Int
        do{
            number = r.nextInt(min,max +1)
            if (number != except) found = true
        }while (!found)
        return number
    }

    private fun newGameStage(){
        initColorList()
    }

    fun openGame(v: View){
        val keyword = getString(R.string.keyword)
        val pass = passwd!!.text.toString()
        if (keyword.equals(pass)){
            passwd!!.visibility = View.INVISIBLE
            submit!!.visibility = View.INVISIBLE
            accessbox!!.visibility = View.VISIBLE
            colorbox!!.visibility = View.VISIBLE
            buttonbox1!!.visibility = View.VISIBLE
            buttonbox2!!.visibility = View.VISIBLE
            scorebox!!.visibility = View.VISIBLE
            progressbox!!.visibility = View.VISIBLE
            Toast.makeText(applicationContext, "Login Success", Toast.LENGTH_LONG).show()
        }else{
            Toast.makeText(applicationContext, "Password is wrong", Toast.LENGTH_LONG).show()
        }
    }

    fun startGame(v: View){
        if (!isStarted) {
            progress!!.progress = 0
            scoreText!!.text = "0"
            start!!.visibility = View.INVISIBLE
            isStarted = true
            newGameStage()
        }
    }

    fun submitColor(v: View){
            val charCode = (v as Button).text.toString()
            if (isStarted){
                if (charCode == charList[clrText!!.text.toString()]){
                    correctSubmit()
                }else{
                    wrongSubmit()
                }
            }
    }

    private fun updateScore(score: Int){
        progress!!.progress = score
        scoreText!!.text = Integer.toString(score)
    }

    private fun correctSubmit(){
        val newScore = progress!!.progress + resources.getInteger(R.integer.counter)
        updateScore(newScore)
        if(progress!!.progress == resources.getInteger(R.integer.maxScore)){
            countDown!!.cancel()
            timer!!.text = "COMPLETE"
            isStarted = false
            start!!.visibility = View.VISIBLE
        }else{
            newGameStage()
        }
    }

    private fun wrongSubmit(){
        if (isMinus!!.isChecked && progress!!.progress > 0) {
            updateScore(progress!!.progress - resources.getInteger(R.integer.counter))
            newGameStage()
        }else{
            newGameStage()
        }
    }
}