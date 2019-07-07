package cigar

import cigar.util.TextAlign
import cigar.util.fillRect
import cigar.util.text
import com.googlecode.lanterna.input.KeyStroke
import com.googlecode.lanterna.terminal.Terminal
import dburyak.pi.ssd1306.Display
import java.awt.Font
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import kotlin.system.measureNanoTime


class Manager(val args: List<String>, val display: Display, val terminal: Terminal,
              var framerate: Float = 30f) {

    @Volatile
    private var nextAvailableProgramID: Long = 0L

    var frameId: Long = 1L
        private set
    var avgFps: Int = 0
        private set
    private var fpsCounter: Int = 0
    private var lastFpsTime: Long = System.currentTimeMillis()

    val homeMenu: HomeMenu = HomeMenu(this)
    val homeMenuProgramTag: ProgramTag

    private val programs: ConcurrentMap<Long, ProgramTag> = ConcurrentHashMap()
    val numRunningPrograms: Int get() = programs.size - 1
    @Volatile
    var activeProgram: Long = 1L
        private set
    @Volatile
    var lastProgramID: Long = activeProgram
        private set
    val activeProgramTag: ProgramTag get() = programs.getValue(activeProgram)

    // Fonts
    /**
     * Fits two lines
     */
    val font2: Font = Font("Dialog", Font.PLAIN, 12)
    /**
     * Fits three lines. Each line is 11 px high.
     */
    val font3: Font = font2.deriveFont(9f)

    init {
        homeMenuProgramTag = spawnProgram(homeMenu)
        homeMenu.show()
    }

    fun getProgramTag(id: Long): ProgramTag? = programs[id]

    fun allProgramTags(): List<ProgramTag> = programs.values.filterNot { it == homeMenuProgramTag }

    fun getNextProgramID(): Long {
        return nextAvailableProgramID++
    }

    fun spawnProgram(program: Program): ProgramTag {
        val tag = ProgramTag(getNextProgramID(), program)
        programs[tag.id] = tag
        return tag
    }

    fun killProgram(programID: Long): Boolean {
        val pTag = programs[programID]
        if (pTag != null) {
            pTag.program.dispose()
            programs.remove(programID)
            if (activeProgram == programID) {
                switchToHomeMenu()
            }
            return true
        }
        return false
    }

    fun switchToProgram(programID: Long): Boolean {
        if (!programs.containsKey(programID))
            return false
        if (activeProgram == programID)
            return false

        activeProgramTag.program.hide()
        lastProgramID = activeProgram
        activeProgram = programID
        activeProgramTag.program.show()
        return true
    }

    fun switchToHomeMenu() {
        switchToProgram(homeMenuProgramTag.id)
    }

    fun switchOutFromHomeMenu(): Boolean {
        return switchToProgram(lastProgramID)
    }

    /**
     * Blocks indefinitely, calling [cycle]
     */
    fun mainLoop() {
        try {
            var currentNano: Long = System.nanoTime()
            while (!Thread.interrupted()) {
                val msBetweenFrames = 1000 / framerate
                val nano = measureNanoTime {
                    cycle((System.nanoTime() - currentNano).coerceAtLeast(1L) / 1_000_000_000.0)
                }
                currentNano = System.nanoTime()
                val sleepMs = msBetweenFrames - nano / 1_000_000
                if (sleepMs > 0) {
                    Thread.sleep(sleepMs.toLong(), ((sleepMs - sleepMs.toLong()) * 1_000_000).toInt())
                }
                frameId++
                fpsCounter++
                if (System.currentTimeMillis() - lastFpsTime >= 1000L) {
                    avgFps = fpsCounter
                    fpsCounter = 0
                    lastFpsTime = System.currentTimeMillis()
                }
            }
        } catch (t: Throwable) {
            t.printStackTrace()
            exceptionHandler(t)
        }
    }

    fun cycle(delta: Double) {
        val keystroke: KeyStroke? = terminal.pollInput()
        if (keystroke != null && (keystroke.character == 'b' && keystroke.isCtrlDown)) {
            if (activeProgramTag == homeMenuProgramTag) {
                switchOutFromHomeMenu()
            } else {
                switchToHomeMenu()
            }
        } else {
            val activeProgram = activeProgram
            if (!programs.containsKey(activeProgram)) {
                this.activeProgram = homeMenuProgramTag.id
            }
            val targetRenderID: Long = activeProgram
            programs.values.forEach { tag ->
                tag.program.cycle(delta, tag.id == targetRenderID, keystroke)
            }
        }
    }

    fun exceptionHandler(throwable: Throwable) {
        display.dim(true).clear()
        display.fillRect(0, 0, display.width(), display.height(), true)
        display.text(font2, "Crash!", display.width() / 2, display.height() / 2 - 8, TextAlign.CENTRE, on = false)
        display.sync()
                .sleep(Duration.ofMillis(33))
                .dim(false)
                .sleep(Duration.ofMillis(50))
                .dim(true)
                .sleep(Duration.ofMillis(33))
                .dim(false)
                .sleep(Duration.ofMillis(50))
                .dim(true)
                .sleep(Duration.ofMillis(33))
                .dim(false)
                .sleep(Duration.ofMillis(50))
                .dim(true)
                .sleep(Duration.ofMillis(33))
                .dim(false)
                .sleep(Duration.ofMillis(50))
                .dim(true)
                .sleep(Duration.ofMinutes(1))
                .invert(true)
                .sleep(Duration.ofMinutes(1))
                .stop()
    }

}