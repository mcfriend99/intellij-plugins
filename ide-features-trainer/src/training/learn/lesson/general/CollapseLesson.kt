// Copyright 2000-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package training.learn.lesson.general

import training.learn.LessonsBundle
import training.learn.interfaces.Module
import training.learn.lesson.kimpl.KLesson
import training.learn.lesson.kimpl.LessonContext
import training.learn.lesson.kimpl.LessonSample

class CollapseLesson(module: Module, lang: String, private val sample: LessonSample)
  : KLesson("Collapse", LessonsBundle.message("collapse.lesson.name"), module, lang) {
  override val lessonContent: LessonContext.() -> Unit
    get() = {
      prepareSample(sample)

      actionTask("CollapseRegion") {
        LessonsBundle.message("collapse.try.collapse", action(it))
      }
      actionTask("ExpandRegion") {
        LessonsBundle.message("collapse.hit.expand", action(it))
      }
      actionTask("CollapseAllRegions") {
        LessonsBundle.message("collapse.all.collapse", action(it))
      }
      actionTask("ExpandAllRegions") {
        LessonsBundle.message("collapse.all.expand", action(it))
      }
    }
}