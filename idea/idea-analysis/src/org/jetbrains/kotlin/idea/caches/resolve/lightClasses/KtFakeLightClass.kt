/*
 * Copyright 2010-2017 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.kotlin.idea.caches.resolve.lightClasses

import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElementFactory
import com.intellij.psi.PsiFile
import com.intellij.psi.impl.light.AbstractLightClass
import org.jetbrains.kotlin.asJava.classes.KtLightClassForSourceDeclaration
import org.jetbrains.kotlin.asJava.classes.LightClassInheritanceHelper
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.idea.KotlinLanguage
import org.jetbrains.kotlin.idea.caches.resolve.resolveToDescriptorIfAny
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.psiUtil.containingClassOrObject
import org.jetbrains.kotlin.resolve.DescriptorUtils
import javax.swing.Icon

// Used as a placeholder when actual light class does not exist
class KtFakeLightClass(val ktClass: KtClassOrObject) : AbstractLightClass(ktClass.manager, KotlinLanguage.INSTANCE) {
    private val _delegate by lazy { PsiElementFactory.SERVICE.getInstance(ktClass.project).createClass(ktClass.name ?: "") }
    private val _containingClass by lazy { ktClass.containingClassOrObject?.let { KtFakeLightClass(it) } }

    override fun getDelegate() = _delegate
    override fun copy() = KtFakeLightClass(ktClass)

    override fun getQualifiedName() = ktClass.fqName?.asString()
    override fun getContainingClass() = _containingClass
    override fun getNavigationElement() = ktClass
    override fun getIcon(flags: Int) = ktClass.getIcon(flags)
    override fun getContainingFile() = ktClass.containingFile

    override fun isInheritor(baseClass: PsiClass, checkDeep: Boolean): Boolean {
        LightClassInheritanceHelper.getService(project).isInheritor(this, baseClass, checkDeep).ifSure { return it }

        val baseKtClass = when (baseClass) {
            is KtLightClassForSourceDeclaration -> baseClass.kotlinOrigin
            is KtFakeLightClass -> baseClass.ktClass
            else -> return false
        }
        val baseDescriptor = baseKtClass.resolveToDescriptorIfAny() as? ClassDescriptor ?: return false
        val thisDescriptor = ktClass.resolveToDescriptorIfAny() as? ClassDescriptor ?: return false
        return if (checkDeep) DescriptorUtils.isSubclass(thisDescriptor, baseDescriptor) else DescriptorUtils.isDirectSubclass(thisDescriptor, baseDescriptor)
    }
}