package org.wikimedia.phpstorm.inheritdoc

import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.psi.elements.Method
import com.jetbrains.php.lang.psi.elements.NewExpression
import com.jetbrains.php.lang.psi.elements.PhpClass

object InheritDocConstructorMatcher {
    private val inheritDocRegex = Regex("""(?i)(?:@inheritdoc\b|\{@inheritdoc\b})""")

    fun isElementInsideInheritDocConstructorCall(element: PsiElement): Boolean {
        var current: NewExpression? = PsiTreeUtil.getParentOfType(element, NewExpression::class.java, false)
        while (current != null) {
            if (hasInheritDocConstructor(current)) {
                return true
            }
            current = PsiTreeUtil.getParentOfType(current, NewExpression::class.java, true)
        }
        return false
    }

    private fun hasInheritDocConstructor(newExpression: NewExpression): Boolean {
        val classReference = newExpression.classReference ?: return false
        val candidates = linkedSetOf<PhpClass>()
        val phpIndex = PhpIndex.getInstance(newExpression.project)

        (classReference.resolve() as? PhpClass)?.let { candidates.add(it) }
        classReference.multiResolve(false)
            .mapNotNull { it.element as? PhpClass }
            .forEach { candidates.add(it) }

        classReference.fqn
            ?.let(::normalizeFqn)
            ?.let { fqn -> phpIndex.getAnyByFQN(fqn) }
            ?.forEach { candidates.add(it) }

        classReference.name
            ?.let(phpIndex::getClassesByName)
            ?.forEach { candidates.add(it) }

        return candidates.any { phpClass ->
            val constructor = phpClass.ownConstructor ?: phpClass.constructor
            hasInheritDocTag(constructor)
        }
    }

    private fun hasInheritDocTag(method: Method?): Boolean {
        val docText = method?.docComment?.text ?: return false
        return inheritDocRegex.containsMatchIn(docText)
    }

    private fun normalizeFqn(fqn: String): String {
        return if (fqn.startsWith("\\")) fqn else "\\$fqn"
    }
}
