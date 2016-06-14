package org.zalando.intellij.swagger.completion.level.field.validator;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.zalando.intellij.swagger.completion.level.string.StringUtils;
import org.zalando.intellij.swagger.completion.traversal.Traversal;

import java.util.Set;
import java.util.stream.Collectors;

public class ValuesValidator {

    private final Traversal traversal;

    private final ReferenceValidator referenceValidator = new ReferenceValidator();

    public ValuesValidator(final Traversal traversal) {
        this.traversal = traversal;
    }

    public void validate(@NotNull final PsiElement psiElement, @NotNull final AnnotationHolder annotationHolder) {
        if (traversal.isValue(psiElement)) {
            if (traversal.isDefinitionRefValue(psiElement)) {
                if (!isFileRef(psiElement)) {
                    referenceValidator.validateDefinitionReference(getDefinitionRefValue(psiElement),
                            getAvailableDefinitions(psiElement), psiElement, annotationHolder);
                }
            } else if (traversal.isParameterRefValue(psiElement)) {
                if (!isFileRef(psiElement)) {
                    referenceValidator.validateParameterReference(getParameterRefValue(psiElement),
                            getAvailableParameters(psiElement), psiElement, annotationHolder);
                }
            }
        }
    }

    private boolean isFileRef(final PsiElement psiElement) {
        return !StringUtils.removeAllQuotes(psiElement.getText()).startsWith("#/");
    }

    private Set<String> getAvailableDefinitions(final PsiElement psiElement) {
        return traversal.getKeyNamesOf("definitions", psiElement.getContainingFile())
                .stream()
                .collect(Collectors.toSet());
    }

    private Set<String> getAvailableParameters(final PsiElement psiElement) {
        return traversal.getKeyNamesOf("parameters", psiElement.getContainingFile())
                .stream()
                .collect(Collectors.toSet());
    }

    private String getParameterRefValue(final PsiElement psiElement) {
        return StringUtils.removeAllQuotes(psiElement.getText())
                .replace("#/parameters/", "");
    }

    private String getDefinitionRefValue(final PsiElement psiElement) {
        return StringUtils.removeAllQuotes(psiElement.getText())
                .replace("#/definitions/", "");
    }
}