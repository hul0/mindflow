// app/src/main/java/com/hul0/mindflow/ui/viewmodel/BreathworkViewModel.kt
package com.hul0.mindflow.ui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow

class BreathworkViewModel : ViewModel() {

    private val _timer = MutableStateFlow("Start")
    val timer: StateFlow<String> = _timer

    private val _instruction = MutableStateFlow("Ready?")
    val instruction: StateFlow<String> = _instruction

    /**
     * A private helper function to update the instruction and timer state.
     */
    private fun updateState(instruction: String, timer: String): Pair<String, String> {
        _instruction.value = instruction
        _timer.value = timer
        return instruction to timer
    }

    /**
     * A generic countdown sequence to be used before each exercise begins.
     */
    private suspend fun readyCountdown(flowCollector: kotlinx.coroutines.flow.FlowCollector<Pair<String, String>>) {
        flowCollector.emit(updateState("Get Ready...", "3"))
        delay(1000)
        flowCollector.emit(updateState("Get Ready...", "2"))
        delay(1000)
        flowCollector.emit(updateState("Get Ready...", "1"))
        delay(1000)
    }

    // --- Existing Breathing Techniques ---

    /**
     * **Box Breathing (4-4-4-4):**
     * Also known as square breathing. Calms the nervous system and reduces stress.
     * Inhale for 4, hold for 4, exhale for 4, hold for 4.
     */
    fun boxBreathing() = flow {
        readyCountdown(this)
        while (true) {
            // Inhale
            for (i in 4 downTo 1) {
                emit(updateState("Breathe In", i.toString()))
                delay(1000)
            }
            // Hold
            for (i in 4 downTo 1) {
                emit(updateState("Hold", i.toString()))
                delay(1000)
            }
            // Exhale
            for (i in 4 downTo 1) {
                emit(updateState("Breathe Out", i.toString()))
                delay(1000)
            }
            // Hold
            for (i in 4 downTo 1) {
                emit(updateState("Hold", i.toString()))
                delay(1000)
            }
        }
    }

    /**
     * **4-7-8 Breathing (Relaxing Breath):**
     * Developed by Dr. Andrew Weil, this technique is deeply relaxing and can help with sleep.
     * Inhale for 4, hold for 7, exhale for 8.
     */
    fun relaxingBreath478() = flow {
        readyCountdown(this)
        while (true) {
            // Inhale
            for (i in 4 downTo 1) {
                emit(updateState("Breathe In", i.toString()))
                delay(1000)
            }
            // Hold
            for (i in 7 downTo 1) {
                emit(updateState("Hold", i.toString()))
                delay(1000)
            }
            // Exhale
            for (i in 8 downTo 1) {
                emit(updateState("Breathe Out", i.toString()))
                delay(1000)
            }
        }
    }

    /**
     * **Wim Hof Breathing:**
     * A method involving rounds of controlled hyperventilation followed by breath retention.
     * Aims to increase energy, reduce stress, and improve immune response.
     */
    fun wimHofBreathing() = flow {
        readyCountdown(this)
        var round = 1
        while (true) {
            emit(updateState("Round $round: 30 Power Breaths", "Begin"))
            delay(2000)
            // 30-40 quick, deep breaths
            for (i in 1..30) {
                emit(updateState("Inhale, then Exhale", i.toString()))
                delay(1500) // Fast pace
            }
            emit(updateState("Final Exhale & Hold", "Empty Lungs"))
            delay(2000)
            // Retention phase
            for (i in 30 downTo 1) { // Example 30s hold
                emit(updateState("Hold & Relax", "${i}s"))
                delay(1000)
            }
            emit(updateState("Recovery Breath", "Inhale Deep & Hold 15s"))
            delay(15000)
            round++
        }
    }

    /**
     * **Triangle Breathing (3-3-3):**
     * A simple technique for focus and calm, similar to Box Breathing but without the final hold.
     * Inhale for 3, hold for 3, exhale for 3.
     */
    fun triangleBreathing() = flow {
        readyCountdown(this)
        while (true) {
            // Inhale
            for (i in 3 downTo 1) {
                emit(updateState("Breathe In", i.toString()))
                delay(1000)
            }
            // Hold
            for (i in 3 downTo 1) {
                emit(updateState("Hold", i.toString()))
                delay(1000)
            }
            // Exhale
            for (i in 3 downTo 1) {
                emit(updateState("Breathe Out", i.toString()))
                delay(1000)
            }
        }
    }

    /**
     * **Coherent Breathing (5-5):**
     * Aims to create coherence in the heart rate rhythm, leading to a balanced state.
     * Inhale for 5, exhale for 5.
     */
    fun coherentBreathing() = flow {
        readyCountdown(this)
        while (true) {
            // Inhale
            for (i in 5 downTo 1) {
                emit(updateState("Breathe In", i.toString()))
                delay(1000)
            }
            // Exhale
            for (i in 5 downTo 1) {
                emit(updateState("Breathe Out", i.toString()))
                delay(1000)
            }
        }
    }

    // --- New Breathing Techniques ---

    /**
     * **Pursed Lip Breathing:**
     * Slows breathing down, relieving shortness of breath and promoting relaxation.
     * Inhale through the nose for 2, exhale slowly through pursed lips for 4.
     */
    fun pursedLipBreathing() = flow {
        readyCountdown(this)
        while (true) {
            // Inhale
            for (i in 2 downTo 1) {
                emit(updateState("Inhale (Nose)", i.toString()))
                delay(1000)
            }
            // Exhale
            for (i in 4 downTo 1) {
                emit(updateState("Exhale (Pursed Lips)", i.toString()))
                delay(1000)
            }
        }
    }

    /**
     * **Diaphragmatic Breathing (Belly Breathing):**
     * Strengthens the diaphragm and encourages full oxygen exchange.
     * Inhale for 4 letting the belly expand, exhale for 6 letting it fall.
     */
    fun diaphragmaticBreathing() = flow {
        readyCountdown(this)
        while (true) {
            // Inhale
            for (i in 4 downTo 1) {
                emit(updateState("Inhale (Belly Out)", i.toString()))
                delay(1000)
            }
            // Exhale
            for (i in 6 downTo 1) {
                emit(updateState("Exhale (Belly In)", i.toString()))
                delay(1000)
            }
        }
    }

    /**
     * **Alternate Nostril Breathing (Nadi Shodhana):**
     * Balances the left and right hemispheres of the brain, calms the mind, and improves focus.
     */
    fun alternateNostrilBreathing() = flow {
        readyCountdown(this)
        while (true) {
            emit(updateState("Block Right Nostril", "Begin"))
            delay(2000)
            for (i in 4 downTo 1) {
                emit(updateState("Inhale Left", i.toString()))
                delay(1000)
            }
            emit(updateState("Block Both Nostrils", "Hold"))
            delay(2000)
            for (i in 4 downTo 1) {
                emit(updateState("Hold", i.toString()))
                delay(1000)
            }
            emit(updateState("Open Right, Exhale", "Release"))
            delay(2000)
            for (i in 6 downTo 1) {
                emit(updateState("Exhale Right", i.toString()))
                delay(1000)
            }
            emit(updateState("Keep Left Blocked", "Begin"))
            delay(2000)
            for (i in 4 downTo 1) {
                emit(updateState("Inhale Right", i.toString()))
                delay(1000)
            }
            emit(updateState("Block Both Nostrils", "Hold"))
            delay(2000)
            for (i in 4 downTo 1) {
                emit(updateState("Hold", i.toString()))
                delay(1000)
            }
            emit(updateState("Open Left, Exhale", "Release"))
            delay(2000)
            for (i in 6 downTo 1) {
                emit(updateState("Exhale Left", i.toString()))
                delay(1000)
            }
        }
    }

    /**
     * **Equal Breathing (Sama Vritti):**
     * A simple, calming breath where the inhale and exhale are of equal length.
     * Inhale for 6, exhale for 6.
     */
    fun equalBreathing66() = flow {
        readyCountdown(this)
        while (true) {
            for (i in 6 downTo 1) {
                emit(updateState("Breathe In", i.toString()))
                delay(1000)
            }
            for (i in 6 downTo 1) {
                emit(updateState("Breathe Out", i.toString()))
                delay(1000)
            }
        }
    }

    /**
     * **Humming Bee Breath (Bhramari):**
     * Calms an agitated mind and can help relieve frustration or anxiety.
     * Inhale for 4, then exhale while making a low-pitched humming sound for 6.
     */
    fun hummingBeeBreath() = flow {
        readyCountdown(this)
        while (true) {
            for (i in 4 downTo 1) {
                emit(updateState("Breathe In", i.toString()))
                delay(1000)
            }
            for (i in 6 downTo 1) {
                emit(updateState("Hummm Out", i.toString()))
                delay(1000)
            }
        }
    }

    /**
     * **Ujjayi Breath (Ocean Breath):**
     * Generates internal heat and builds energy while calming the mind.
     * Inhale and exhale through the nose with a slight constriction in the throat, creating an "ocean" sound.
     */
    fun ujjayiBreath() = flow {
        readyCountdown(this)
        while (true) {
            for (i in 5 downTo 1) {
                emit(updateState("Inhale (Ocean Sound)", i.toString()))
                delay(1000)
            }
            for (i in 5 downTo 1) {
                emit(updateState("Exhale (Ocean Sound)", i.toString()))
                delay(1000)
            }
        }
    }

    /**
     * **Kapalabhati (Skull Shining Breath):**
     * An energizing and cleansing practice involving forceful exhales and passive inhales.
     */
    fun kapalabhatiBreathing() = flow {
        readyCountdown(this)
        var round = 1
        while (true) {
            emit(updateState("Round $round: 30 quick exhales", "Begin"))
            delay(2000)
            for (i in 1..30) {
                emit(updateState("Forceful Exhale", i.toString()))
                delay(750) // Very fast pace
            }
            emit(updateState("Recovery", "Breathe Normally"))
            delay(15000)
            round++
        }
    }

    /**
     * **Extended Exhale (4-8):**
     * Activates the parasympathetic nervous system (rest and digest) by making the exhale longer than the inhale.
     * Inhale for 4, exhale for 8.
     */
    fun extendedExhale48() = flow {
        readyCountdown(this)
        while (true) {
            for (i in 4 downTo 1) {
                emit(updateState("Breathe In", i.toString()))
                delay(1000)
            }
            for (i in 8 downTo 1) {
                emit(updateState("Breathe Out", i.toString()))
                delay(1000)
            }
        }
    }

    /**
     * **Buteyko Method (Simplified):**
     * Focuses on breath reduction to improve oxygenation of tissues. This is a simplified version.
     * Inhale 3, exhale 4, then hold the breath out for 5.
     */
    fun buteykoHold() = flow {
        readyCountdown(this)
        while (true) {
            for (i in 3 downTo 1) {
                emit(updateState("Breathe In Gently", i.toString()))
                delay(1000)
            }
            for (i in 4 downTo 1) {
                emit(updateState("Breathe Out Gently", i.toString()))
                delay(1000)
            }
            for (i in 5 downTo 1) {
                emit(updateState("Hold (Empty)", i.toString()))
                delay(1000)
            }
        }
    }

    /**
     * **Progressive Relaxation Breathing:**
     * Combines breathing with muscle tension and release to achieve deep physical relaxation.
     * Inhale for 4 while tensing a muscle group, exhale for 6 while releasing.
     */
    fun progressiveRelaxationBreathing() = flow {
        readyCountdown(this)
        emit(updateState("Focus on your Feet", "Prepare"))
        delay(3000)
        while (true) {
            // This is a conceptual loop. In a real app, you'd guide through muscle groups.
            for (i in 4 downTo 1) {
                emit(updateState("Inhale & Tense Muscles", i.toString()))
                delay(1000)
            }
            for (i in 6 downTo 1) {
                emit(updateState("Exhale & Release", i.toString()))
                delay(1000)
            }
            emit(updateState("Move to Next Muscle Group", "Relax"))
            delay(3000)
        }
    }

    /**
     * **Cyclic Sighing (Physiological Sigh):**
     * A powerful technique to quickly reduce high levels of stress and anxiety.
     * Two inhales through the nose followed by a long exhale through the mouth.
     */
    fun cyclicSighing() = flow {
        readyCountdown(this)
        while (true) {
            for (i in 4 downTo 1) {
                emit(updateState("Deep Inhale", i.toString()))
                delay(1000)
            }
            emit(updateState("Sip More Air In", "Top Off"))
            delay(1000)
            for (i in 8 downTo 1) {
                emit(updateState("Long Exhale (Mouth)", i.toString()))
                delay(1000)
            }
        }
    }

    /**
     * **7-11 Breathing:**
     * A simple technique for anxiety where the exhale is significantly longer than the inhale.
     * Inhale for 7, exhale for 11.
     */
    fun sevenElevenBreathing() = flow {
        readyCountdown(this)
        while (true) {
            for (i in 7 downTo 1) {
                emit(updateState("Breathe In", i.toString()))
                delay(1000)
            }
            for (i in 11 downTo 1) {
                emit(updateState("Breathe Out", i.toString()))
                delay(1000)
            }
        }
    }

    /**
     * **Three-Part Breath (Dirga Pranayama):**
     * Fills the lungs completely, increasing oxygen capacity and promoting awareness of the body.
     * Inhale into the belly, then ribs, then chest. Exhale from chest, then ribs, then belly.
     */
    fun threePartBreath() = flow {
        readyCountdown(this)
        while (true) {
            for (i in 3 downTo 1) {
                emit(updateState("Inhale to Belly", i.toString()))
                delay(1000)
            }
            for (i in 3 downTo 1) {
                emit(updateState("Inhale to Ribs", i.toString()))
                delay(1000)
            }
            for (i in 6 downTo 1) {
                emit(updateState("Exhale Slowly", i.toString()))
                delay(1000)
            }
        }
    }

    /**
     * **Cooling Breath (Sitali):**
     * A yoga breathing technique intended to cool the body and calm the mind.
     * Inhale through a curled tongue, exhale through the nose.
     */
    fun coolingBreathSitali() = flow {
        readyCountdown(this)
        while (true) {
            for (i in 5 downTo 1) {
                emit(updateState("Inhale (Curled Tongue)", i.toString()))
                delay(1000)
            }
            for (i in 6 downTo 1) {
                emit(updateState("Exhale (Nose)", i.toString()))
                delay(1000)
            }
        }
    }

    /**
     * **Lion's Breath (Simhasana):**
     * A powerful breath used to relieve tension in the face and chest.
     * Inhale through the nose, then exhale forcefully with a "ha" sound, sticking the tongue out.
     */
    fun lionsBreath() = flow {
        readyCountdown(this)
        while (true) {
            for (i in 3 downTo 1) {
                emit(updateState("Inhale (Nose)", i.toString()))
                delay(1000)
            }
            emit(updateState("Exhale 'HA!'", "Tongue Out"))
            delay(3000)
        }
    }
}
