package fridge.lifebattle

import fridge.lifebattle.board.Coordinates
import fridge.lifebattle.board.LifeBoard
import fridge.lifebattle.rule.Rule
import fridge.lifebattle.rule.ruleFromString
import javafx.animation.Timeline
import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.stage.Stage
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.animation.KeyFrame
import javafx.geometry.Insets
import javafx.scene.control.Label
import javafx.scene.control.ScrollPane
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.CornerRadii
import javafx.util.Duration
import kotlin.random.Random

const val BOARD_SIZE = 100
const val INITIAL_CELL_RULE = "B1/S23"

fun main() {
//    val board = LifeBoard(200)
//    val gol = ruleFromString("B12345678/S012345678")
//
//    listOf(
//        3 to 3
//    ).forEach { pair -> board[coordinatesFrom(pair)] = gol }
//
//    board.draw()
//    repeat(10000) {
//        board.step()
//        println(List(ceil(board.width * 1.75).roundToInt()) { '=' }.joinToString(""))
//        board.draw()
//    }
//
//    println()
//
//    val census = mutableMapOf<Rule, Int>()
//    board.values.forEach { rule -> census[rule] = census.getOrElse(rule) { 0 } + 1 }
//    for ((rule, count) in census.entries.sortedByDescending { pair -> pair.value }){
//        println("[$rule]: $count")
//    }

    Application.launch(App::class.java)
}

val colorsCache = mutableMapOf<Rule, Color>()
fun colorFrom(rule: Rule): Color {
    if (rule in colorsCache) return colorsCache.getValue(rule)

    val random = Random(rule.hashCode())

    fun getVal(): Double {
        return random.nextDouble(0.9)
    }
    val color = Color.color(
        getVal(),
        getVal(),
        getVal()
    )
    colorsCache[rule] = color
    return color
}

fun GraphicsContext.workingFill() {
    val canvas = this.canvas
    this.fillRect(0.0, 0.0, canvas.width, canvas.height)
}

class App : Application() {
    private val canvas = Canvas(1000.0, 1000.0)
    val graphics: GraphicsContext = canvas.graphicsContext2D

    val board = LifeBoard(BOARD_SIZE)
    init {
        board[Coordinates(0, 0)] = ruleFromString(INITIAL_CELL_RULE)
    }

    val cellWidth = canvas.width / board.width
    val cellHeight = canvas.height / board.height
    val padding = 0.05

    val timeline = Timeline(KeyFrame(Duration.seconds(0.1), {
        board.step()
        draw()
    }))

    val ruleListStage = Stage()
    val ruleListPane = ScrollPane()
    init {
        ruleListStage.height = 500.0
        ruleListStage.width = 200.0

        ruleListPane.content = VBox()
        ruleListStage.scene = Scene(ruleListPane)
    }

    override fun start(primaryStage: Stage?) {
        if (primaryStage == null) return

        draw()

        primaryStage.scene = Scene(VBox(canvas))
        primaryStage.show()

        ruleListStage.show()

        timeline.playFromStart()
    }

    fun draw() {
        graphics.fill = Color.WHITE
        graphics.workingFill()

        for ((coordinates, rule) in board) {
            if (rule == null) continue

            graphics.fill = colorFrom(rule)
            graphics.fillRect(
                coordinates.x * cellWidth + (cellWidth * padding),
                coordinates.y * cellHeight + (cellHeight * padding),
                cellWidth - (cellWidth * padding) * 2,
                cellHeight - (cellWidth * padding) * 2
            )
        }

        val ruleContainer = ruleListPane.content as VBox
        ruleContainer.children.clear()
        val ruleCensus = mutableMapOf<Rule, Int>()
        for (rule in board.values) ruleCensus[rule] = ruleCensus.getOrPut(rule) { 0 } + 1
        for ((rule, count) in ruleCensus.entries.sortedByDescending { (_, count) -> count }) {
            val label = Label("$rule ($count)")
            label.background = Background(BackgroundFill(colorFrom(rule), CornerRadii.EMPTY, Insets.EMPTY))
            ruleContainer.children.add(label)
        }

        timeline.playFromStart()
    }
}
