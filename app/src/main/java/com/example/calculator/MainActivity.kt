package com.example.calculator

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private lateinit var numerator1EditText: EditText
    private lateinit var denominator1EditText: EditText
    private lateinit var numerator2EditText: EditText
    private lateinit var denominator2EditText: EditText

    private lateinit var plusButton: Button
    private lateinit var minusButton: Button
    private lateinit var multiplyButton: Button
    private lateinit var divideButton: Button
    private lateinit var decimalButton: Button

    private lateinit var wholeTextView: TextView
    private lateinit var numeratorTextView: TextView
    private lateinit var denominatorTextView: TextView
    private lateinit var fullNumberTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize views
        numerator1EditText = findViewById(R.id.numerator1EditText)
        denominator1EditText = findViewById(R.id.denominator1EditText)
        numerator2EditText = findViewById(R.id.numerator2EditText)
        denominator2EditText = findViewById(R.id.denominator2EditText)

        plusButton = findViewById(R.id.plusButton)
        minusButton = findViewById(R.id.minusButton)
        multiplyButton = findViewById(R.id.multiplyButton)
        divideButton = findViewById(R.id.divideButton)

        wholeTextView = findViewById(R.id.wholeTextView)
        numeratorTextView = findViewById(R.id.numeratorTextView)
        denominatorTextView = findViewById(R.id.denominatorTextView)
        fullNumberTextView = findViewById(R.id.fullNumberView)

        // Set listeners
        setEditTextListeners()

        plusButton.setOnClickListener { calculate('+') }
        minusButton.setOnClickListener { calculate('-') }
        multiplyButton.setOnClickListener { calculate('*') }
        divideButton.setOnClickListener { calculate('/') }
    }

    private fun setEditTextListeners() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                // Ensure only numbers are allowed
                s?.let {
                    if (!it.toString().matches(Regex("[0-9]*"))) {
                        it.delete(it.length - 1, it.length)
                    }
                }
            }
        }

        numerator1EditText.addTextChangedListener(textWatcher)
        denominator1EditText.addTextChangedListener(textWatcher)
        numerator2EditText.addTextChangedListener(textWatcher)
        denominator2EditText.addTextChangedListener(textWatcher)
    }

    private fun calculate(operation: Char) {
        val num1 = numerator1EditText.text.toString().toInt()
        val den1 = denominator1EditText.text.toString().toInt()
        val num2 = numerator2EditText.text.toString().toInt()
        val den2 = denominator2EditText.text.toString().toInt()

        // Создаем объекты Fraction из введенных значений
        val fraction1 = Fraction(0, num1, den1)
        val fraction2 = Fraction(0, num2, den2)

        val result: Fraction = when (operation) {
            '+' -> fraction1 + fraction2
            '-' -> fraction1 - fraction2
            '*' -> fraction1 * fraction2
            '/' -> fraction1 / fraction2
            else -> Fraction(0, 1)
        }

        displayResult(result)
    }

    @SuppressLint("SetTextI18n")
    private fun displayResult(result: Fraction) {
        wholeTextView.text = "Целых: " + result.whole.toString()
        numeratorTextView.text = "Числитель: " + result.numerator.toString()
        denominatorTextView.text = "Знаменатель: " + result.denominator.toString()
        fullNumberTextView.text = "Дробный вид: " + result.ToFloat()
    }

}

data class Fraction(val whole: Int, val numerator: Int, val denominator: Int = 1) {

    init {
        require(denominator != 0) { "zero den" }
    }

    operator fun plus(other: Fraction): Fraction {
        val commonDenominator = lcm(this.denominator, other.denominator)
        val resultNumerator =
            ((this.whole * (commonDenominator) + (this.numerator * commonDenominator / this.denominator))
                + ((other.whole * (commonDenominator)) + (other.numerator * commonDenominator / other.denominator)))
        return simplifyFraction(Fraction(0, resultNumerator, commonDenominator))
    }

    operator fun minus(other: Fraction): Fraction {
        val commonDenominator = lcm(this.denominator, other.denominator)
        val resultNumerator =
            ((this.whole * (commonDenominator) + (this.numerator * commonDenominator / this.denominator))
                    - ((other.whole * (commonDenominator)) + (other.numerator * commonDenominator / other.denominator)))
        return simplifyFraction(Fraction(0, resultNumerator, commonDenominator))
    }

    operator fun times(other: Fraction): Fraction {
        val resultNumerator =
            (this.whole * this.denominator + this.numerator) * (other.whole * other.denominator + other.numerator)
        val resultDenominator = this.denominator * other.denominator
        return simplifyFraction(Fraction(0, resultNumerator, resultDenominator))
    }

    operator fun div(other: Fraction): Fraction {
        val resultNumerator = (this.whole * this.denominator + this.numerator)  * (other.denominator)
        val resultDenominator = (this.denominator) * (other.whole * other.denominator + other.numerator)
        return simplifyFraction(Fraction(0, resultNumerator, resultDenominator))
    }

    public fun ToFloat(): Float{
        //val formattedDecimal = String.format("%.2f", decimalValue)
        return this.whole + this.numerator / this.denominator.toFloat()
    }

    private fun simplifyFraction(fraction: Fraction): Fraction {
        val gcd = gcd(fraction.numerator, fraction.denominator)
        return if (gcd != 0) {
            Fraction(
                fraction.whole + fraction.numerator / fraction.denominator,
                fraction.numerator % fraction.denominator,
                fraction.denominator / gcd
            )
        } else {
            fraction
        }
    }

    private fun gcd(a: Int, b: Int): Int {
        var tempA = a
        var tempB = b
        while (tempB != 0) {
            val temp = tempB
            tempB = tempA % tempB
            tempA = temp
        }
        return tempA
    }

    private fun lcm(a: Int, b: Int): Int {
        return a / gcd(a, b) * b
    }
}