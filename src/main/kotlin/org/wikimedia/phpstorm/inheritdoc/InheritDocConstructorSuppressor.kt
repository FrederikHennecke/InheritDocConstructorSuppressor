package org.wikimedia.phpstorm.inheritdoc

import com.intellij.codeInspection.InspectionSuppressor
import com.intellij.codeInspection.SuppressQuickFix
import com.intellij.psi.PsiElement

class InheritDocConstructorSuppressor : InspectionSuppressor {
    override fun isSuppressedFor(element: PsiElement, toolId: String): Boolean {
        if (toolId !in SUPPORTED_TOOL_IDS) {
            return false
        }

        return InheritDocConstructorMatcher.isElementInsideInheritDocConstructorCall(element)
    }

    override fun getSuppressActions(element: PsiElement?, toolId: String): Array<SuppressQuickFix> {
        return emptyArray()
    }

}

val SUPPORTED_TOOL_IDS = setOf(
	"PhpParamsInspection",
	"PhpWrongParamTypeInspection",
	"PhpStrictTypeCheckingInspection"
)
