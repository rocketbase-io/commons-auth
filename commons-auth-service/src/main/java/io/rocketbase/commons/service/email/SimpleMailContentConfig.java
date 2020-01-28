package io.rocketbase.commons.service.email;

import io.rocketbase.commons.config.EmailProperties;
import io.rocketbase.commons.email.EmailTemplateBuilder;
import io.rocketbase.commons.email.model.HtmlTextEmail;
import io.rocketbase.commons.email.template.ColorStyle;
import io.rocketbase.commons.model.AppInviteEntity;
import io.rocketbase.commons.model.AppUserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.util.StringUtils;

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
        return messageSource.getMessage("auth.email.register.subject", new Object[]{emailProperties.getSubjectPrefix()}, currentLocale).trim();
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
        return messageSource.getMessage("auth.email.forgot.subject", new Object[]{emailProperties.getSubjectPrefix()}, currentLocale).trim();
    }

    @Override
    public HtmlTextEmail invite(AppInviteEntity invite, String actionUrl) {
        Locale currentLocale = LocaleContextHolder.getLocale();
        EmailTemplateBuilder.EmailTemplateConfigBuilder builder = EmailTemplateBuilder.builder()
                .header(messageSource.getMessage("auth.email.invite.header", new Object[]{emailProperties.getServiceName()}, currentLocale)).color(new ColorStyle("fff", "7FA162")).and()
                .addText(messageSource.getMessage("auth.email.invite.welcome", new Object[]{invite.getDisplayName()}, currentLocale)).and()
                .addText(messageSource.getMessage("auth.email.invite.youHaveInvitedBy", new Object[]{invite.getInvitor(), emailProperties.getServiceName()}, currentLocale)).and();

        if (StringUtils.isEmpty(invite.getMessage())) {
            builder.addHtml("&nbsp;").and()
                    .addText(messageSource.getMessage("auth.email.invite.messageFrom", new Object[]{invite.getInvitor()}, currentLocale)).center().italic().and()
                    .addText(invite.getMessage()).center().italic().bold().and()
                    .addHtml("&nbsp;");
        }

        return builder
                .addText(messageSource.getMessage("auth.email.invite.createAccount", new Object[]{}, currentLocale)).and()
                .addButton(messageSource.getMessage("auth.email.invite.button", new Object[]{}, currentLocale), actionUrl).color(new ColorStyle("fff", "7FA162")).center().and()
                .addText(String.format("- %s", emailProperties.getServiceName())).and()
                .addFooter(String.format("%s<br>%s",
                        messageSource.getMessage("auth.email.invite.footer.youReceiveThisEmailBecause", new Object[]{invite.getInvitor(), emailProperties.getServiceName()}, currentLocale),
                        messageSource.getMessage("auth.email.invite.footer.ifYouAreNotSureWhy", new Object[]{emailProperties.getSupportEmail()}, currentLocale))).and()
                .copyright(emailProperties.getCopyrightName()).url(emailProperties.getCopyrightUrl())
                .build();
    }

    @Override
    public String inviteSubject(AppInviteEntity invite) {
        Locale currentLocale = LocaleContextHolder.getLocale();
        return messageSource.getMessage("auth.email.invite.subject", new Object[]{emailProperties.getSubjectPrefix(), invite.getInvitor()}, currentLocale).trim();
    }
}
