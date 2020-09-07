// Copyright 2000-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package training.learn.lesson.python.completion

import com.intellij.testGuiFramework.framework.GuiTestUtil
import com.intellij.testGuiFramework.util.Key
import training.commands.kotlin.TaskContext
import training.commands.kotlin.TaskRuntimeContext
import training.learn.LessonsBundle
import training.learn.interfaces.Module
import training.learn.lesson.kimpl.*
import training.learn.lesson.kimpl.LessonUtil.checkExpectedStateOfEditor
import javax.swing.JList

class PythonTabCompletionLesson(module: Module)
  : KLesson("Tab completion", LessonsBundle.message("python.tab.completion.lesson.name"), module, "Python") {
  private val template = parseLessonSample("""
    class Calculator:
        def __init__(self):
            self.current = 0
            self.total = 0
    
        def add(self, amount):
            self.current += amount
    
        def get_current(self):
            return self.<caret>
  """.trimIndent())

  private val sample = createFromTemplate(template, "current")

  private val isTotalItem = { item: Any -> item.toString().contains("total") }

  override val lessonContent: LessonContext.() -> Unit
    get() {
      return {
        prepareSample(sample)
        task("CodeCompletion") {
          text(LessonsBundle.message("python.tab.completion.start.completion",
                                     code("current"), code("total"), action(it)))
          triggerByListItemAndHighlight(checkList = { ui -> isTotalItem(ui) })
          proposeRestoreMe()
          test { actions(it) }
        }
        task {
          text(LessonsBundle.message("python.tab.completion.select.item", code("total")))
          restoreState {
            (previous.ui as? JList<*>)?.let { ui ->
              !ui.isShowing || LessonUtil.findItem(ui, isTotalItem) == null
            } ?: true
          }
          stateCheck {
            selectNeededItem() ?: false
          }
          test {
            ideFrame {
              jListContains("total").item("total").click()
            }
          }
        }
        task {
          val result = LessonUtil.insertIntoSample(template, "total")
          text(LessonsBundle.message("python.tab.completion.use.tab.completion",
                                     action("EditorEnter"), code("total"), code("current"),
                                     action("EditorTab")))

          trigger("EditorChooseLookupItemReplace") {
            editor.document.text == result
          }
          restoreState {
            selectNeededItem()?.not() ?: true
          }
          test { GuiTestUtil.shortcut(Key.TAB) }
        }
      }
    }

  private fun TaskRuntimeContext.selectNeededItem(): Boolean? {
    return (previous.ui as? JList<*>)?.let { ui ->
      if (!ui.isShowing) return false
      val selectedIndex = ui.selectedIndex
      selectedIndex != -1 && isTotalItem(ui.model.getElementAt(selectedIndex))
    }
  }

  private fun TaskContext.proposeRestoreMe() {
    proposeRestore {
      checkExpectedStateOfEditor(sample)
    }
  }
}