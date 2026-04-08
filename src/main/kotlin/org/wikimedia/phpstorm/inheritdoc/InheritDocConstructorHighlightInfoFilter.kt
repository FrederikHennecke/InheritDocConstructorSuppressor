package org.wikimedia.phpstorm.inheritdoc

import com.intellij.codeInsight.daemon.impl.HighlightInfo
import com.intellij.codeInsight.daemon.impl.HighlightInfoFilter
import com.intellij.psi.PsiFile

class InheritDocConstructorHighlightInfoFilter : HighlightInfoFilter {
    override fun accept(highlightInfo: HighlightInfo, file: PsiFile?): Boolean {
        if (file == null) {
            return true
        }

        if (!isTargetWarning(highlightInfo)) {
            return true
        }

        val element = file.findElementAt(highlightInfo.startOffset) ?: return true
        return !InheritDocConstructorMatcher.isElementInsideInheritDocConstructorCall(element)
    }

    private fun isTargetWarning(highlightInfo: HighlightInfo): Boolean {
        val inspectionId = highlightInfo.inspectionToolId
        if (inspectionId in PARAM_TYPE_INSPECTION_IDS) {
            return true
        }

        val description = highlightInfo.description ?: return false
        return description.contains(EXPECTED_PARAMETER_PREFIX)
    }

    private companion object {
        const val EXPECTED_PARAMETER_PREFIX = "Expected parameter of type"
	}
}

val PARAM_TYPE_INSPECTION_IDS = setOf(
	"PhpParamsInspection",
	"PhpWrongParamTypeInspection",
	"PhpStrictTypeCheckingInspection"
)
