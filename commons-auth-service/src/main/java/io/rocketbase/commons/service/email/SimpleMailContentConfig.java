package io.rocketbase.commons.service.email;

import io.rocketbase.commons.config.EmailProperties;
import io.rocketbase.commons.email.EmailTemplateBuilder;
import io.rocketbase.commons.email.model.HtmlTextEmail;
import io.rocketbase.commons.email.template.ColorStyle;
import io.rocketbase.commons.model.AppUserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

@RequiredArgsConstructor
public class SimpleMailContentConfig implements MailContentConfig {

    final EmailProperties emailProperties;
    final MessageSource messageSource;

    @Override
    public HtmlTextEmail register(AppUserEntity user, String actionUrl) {
        Locale currentLocale = LocaleContextHolder.getLocale();
        return EmailTemplateBuilder.builder()
                .header(messageSource.getMessage("auth.email.register.header", new Object[]{}, currentLocale)).and()
                .addText(messageSource.getMessage("auth.email.register.hi", new Object[]{user.getUsername()}, currentLocale)).and()
                .addText(messageSource.getMessage("auth.email.register.pleaseVerifyAccount", new Object[]{}, currentLocale)).and()
                .addButton(messageSource.getMessage("auth.email.register.button", new Object[]{}, currentLocale), actionUrl).center().and()
                .addText(String.format("- %s", emailProperties.getServiceName())).and()
                .addFooter(String.format("%s<br>%s",
                        messageSource.getMessage("auth.email.footer.youReceiveThisEmailBecause", new Object[]{emailProperties.getServiceName()}, currentLocale),
                        messageSource.getMessage("auth.email.footer.ifYouAreNotSureWhy", new Object[]{emailProperties.getSupportEmail()}, currentLocale))).and()
                .copyright(emailProperties.getCopyrightName()).url(emailProperties.getCopyrightUrl())
                .build();
    }

    @Override
    public String registerSubject(AppUserEntity user) {
        Locale currentLocale = LocaleContextHolder.getLocale();
        return messageSource.getMessage("auth.email.register.subject", new Object[]{emailProperties.getSubjectPrefix()}, currentLocale);
    }

    @Override
    public HtmlTextEmail forgotPassword(AppUserEntity user, String actionUrl) {
        Locale currentLocale = LocaleContextHolder.getLocale();
        return EmailTemplateBuilder.builder()
                .header(messageSource.getMessage("auth.email.forgot.header", new Object[]{}, currentLocale)).color(new ColorStyle("fff", "E63946")).and()
                .addText(messageSource.getMessage("auth.email.forgot.hi", new Object[]{user.getUsername()}, currentLocale)).and()
                .addText(messageSource.getMessage("auth.email.forgot.confirmPasswordChange", new Object[]{}, currentLocale)).and()
                .addButton(messageSource.getMessage("auth.email.forgot.button", new Object[]{}, currentLocale), actionUrl).color(new ColorStyle("fff", "E63946")).center().and()
                .addText(String.format("- %s", emailProperties.getServiceName())).and()
                .addFooter(String.format("%s<br>%s",
                        messageSource.getMessage("auth.email.footer.youReceiveThisEmailBecause", new Object[]{emailProperties.getServiceName()}, currentLocale),
                        messageSource.getMessage("auth.email.footer.ifYouAreNotSureWhy", new Object[]{emailProperties.getSupportEmail()}, currentLocale))).and()
                .copyright(emailProperties.getCopyrightName()).url(emailProperties.getCopyrightUrl())
                .build();
    }

    @Override
    public String forgotPasswordSubject(AppUserEntity user) {
        Locale currentLocale = LocaleContextHolder.getLocale();
        return messageSource.getMessage("auth.email.forgot.subject", new Object[]{emailProperties.getSubjectPrefix()}, currentLocale);
    }
}
