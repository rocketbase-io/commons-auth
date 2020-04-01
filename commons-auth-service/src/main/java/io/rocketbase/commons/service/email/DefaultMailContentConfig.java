package io.rocketbase.commons.service.email;

import io.rocketbase.commons.config.EmailProperties;
import io.rocketbase.commons.email.EmailTemplateBuilder;
import io.rocketbase.commons.email.EmailTemplateBuilder.EmailTemplateConfigBuilder;
import io.rocketbase.commons.email.model.HtmlTextEmail;
import io.rocketbase.commons.email.template.ColorStyle;
import io.rocketbase.commons.model.AppInviteEntity;
import io.rocketbase.commons.model.AppUserReference;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.util.StringUtils;

import java.util.Locale;

import static io.rocketbase.commons.service.email.DefaultMailContentConfig.MailType.*;

@RequiredArgsConstructor
public class DefaultMailContentConfig implements MailContentConfig {

    final EmailProperties emailProperties;
    final MessageSource messageSource;

    @Override
    public HtmlTextEmail register(AppUserReference user, String actionUrl) {
        Locale currentLocale = LocaleContextHolder.getLocale();
        EmailTemplateConfigBuilder builder = EmailTemplateBuilder.builder();
        // header
        buildHeader(REGISTER, builder, messageSource.getMessage("auth.email.register.header", new Object[]{}, currentLocale));
        // intro text
        builder.addText(messageSource.getMessage("auth.email.register.hi", new Object[]{user.getUsername()}, currentLocale)).and()
                .addText(messageSource.getMessage("auth.email.register.pleaseVerifyAccount", new Object[]{}, currentLocale));
        // button
        buildButton(REGISTER, builder, messageSource.getMessage("auth.email.register.button", new Object[]{}, currentLocale), actionUrl);
        // greeting / footer...
        return builder
                .addText(getGreeting()).and()
                .addFooter(getFooter(currentLocale)).center().and()
                .copyright(emailProperties.getCopyrightName()).url(emailProperties.getCopyrightUrl())
                .build();
    }

    @Override
    public String registerSubject(AppUserReference user) {
        Locale currentLocale = LocaleContextHolder.getLocale();
        return messageSource.getMessage("auth.email.register.subject", new Object[]{emailProperties.getSubjectPrefix()}, currentLocale).trim();
    }

    @Override
    public HtmlTextEmail forgotPassword(AppUserReference user, String actionUrl) {
        Locale currentLocale = LocaleContextHolder.getLocale();
        EmailTemplateConfigBuilder builder = EmailTemplateBuilder.builder();
        // header
        buildHeader(FORGOT, builder, messageSource.getMessage("auth.email.forgot.header", new Object[]{}, currentLocale));
        // intro text
        builder.addText(messageSource.getMessage("auth.email.forgot.hi", new Object[]{user.getUsername()}, currentLocale)).and()
                .addText(messageSource.getMessage("auth.email.forgot.confirmPasswordChange", new Object[]{}, currentLocale));
        // button
        buildButton(FORGOT, builder, messageSource.getMessage("auth.email.forgot.button", new Object[]{}, currentLocale), actionUrl);
        // greeting / footer...
        return builder.addText(getGreeting()).and()
                .addFooter(getFooter(currentLocale)).center().and()
                .copyright(emailProperties.getCopyrightName()).url(emailProperties.getCopyrightUrl())
                .build();
    }

    @Override
    public String forgotPasswordSubject(AppUserReference user) {
        Locale currentLocale = LocaleContextHolder.getLocale();
        return messageSource.getMessage("auth.email.forgot.subject", new Object[]{emailProperties.getSubjectPrefix()}, currentLocale).trim();
    }

    @Override
    public HtmlTextEmail invite(AppInviteEntity invite, String actionUrl) {
        Locale currentLocale = LocaleContextHolder.getLocale();
        EmailTemplateConfigBuilder builder = EmailTemplateBuilder.builder();
        // header
        buildHeader(INVITE, builder, messageSource.getMessage("auth.email.invite.header", new Object[]{getServiceName()}, currentLocale));
        // intro text
        builder.addText(messageSource.getMessage("auth.email.invite.welcome", new Object[]{invite.getDisplayName()}, currentLocale)).and()
                .addText(messageSource.getMessage("auth.email.invite.youHaveInvitedBy", new Object[]{invite.getInvitor(), getServiceName()}, currentLocale)).and();
        // invite message
        if (!StringUtils.isEmpty(invite.getMessage())) {
            builder.addHtml("&nbsp;").and()
                    .addText(messageSource.getMessage("auth.email.invite.messageFrom", new Object[]{invite.getInvitor()}, currentLocale)).center().italic().and()
                    .addText(invite.getMessage()).center().italic().bold().and()
                    .addHtml("&nbsp;");
        }
        builder.addText(messageSource.getMessage("auth.email.invite.createAccount", new Object[]{}, currentLocale));
        // button
        buildButton(INVITE, builder, messageSource.getMessage("auth.email.invite.button", new Object[]{}, currentLocale), actionUrl);
        // greeting / footer...
        return builder.addText(getGreeting()).and()
                .addFooter(getFooterInvite(invite, currentLocale)).center().and()
                .copyright(emailProperties.getCopyrightName()).url(emailProperties.getCopyrightUrl())
                .build();
    }

    @Override
    public String inviteSubject(AppInviteEntity invite) {
        Locale currentLocale = LocaleContextHolder.getLocale();
        return messageSource.getMessage("auth.email.invite.subject", new Object[]{emailProperties.getSubjectPrefix(), invite.getInvitor()}, currentLocale).trim();
    }

    protected ColorStyle getButtonColor(MailType type) {
        switch (type) {
            case REGISTER:
                return ColorStyle.BASE_STYLE;
            case FORGOT:
                return new ColorStyle("fff", "E63946");
            case INVITE:
                return new ColorStyle("fff", "7FA162");
        }
        return ColorStyle.BASE_STYLE;
    }

    protected ColorStyle getHeaderColor(MailType type) {
        switch (type) {
            case REGISTER:
                return ColorStyle.BASE_STYLE;
            case FORGOT:
                return new ColorStyle("fff", "E63946");
            case INVITE:
                return new ColorStyle("fff", "7FA162");
        }
        return ColorStyle.BASE_STYLE;
    }

    protected void buildHeader(MailType type, EmailTemplateConfigBuilder builder, String text) {
        prependHeader(type, builder);
        builder.header(text).color(getHeaderColor(type));
        appendHeader(type, builder);
    }

    protected void buildButton(MailType type, EmailTemplateConfigBuilder builder, String text, String actionUrl) {
        builder.addButton(text, actionUrl).color(getButtonColor(type)).center();
        appendButton(type, builder);
    }

    /**
     * could be used to add extra before header
     */
    protected void prependHeader(MailType type, EmailTemplateConfigBuilder builder) {
    }

    /**
     * could be used to add extra after header
     */
    protected void appendHeader(MailType type, EmailTemplateConfigBuilder builder) {
    }

    /**
     * could be used to add extra after button
     */
    protected void appendButton(MailType type, EmailTemplateConfigBuilder builder) {
    }


    /**
     * last text-line before footer
     */
    protected String getGreeting() {
        return String.format("- %s", getServiceName());
    }

    protected String getServiceName() {
        return emailProperties.getServiceName();
    }


    protected String getFooter(Locale currentLocale) {
        return String.format("%s %s",
                messageSource.getMessage("auth.email.footer.youReceiveThisEmailBecause", new Object[]{getServiceName()}, currentLocale),
                messageSource.getMessage("auth.email.footer.ifYouAreNotSureWhy", new Object[]{emailProperties.getSupportEmail()}, currentLocale));
    }

    protected String getFooterInvite(AppInviteEntity invite, Locale currentLocale) {
        return String.format("%s %s",
                messageSource.getMessage("auth.email.invite.footer.youReceiveThisEmailBecause", new Object[]{invite.getInvitor(), getServiceName()}, currentLocale),
                messageSource.getMessage("auth.email.invite.footer.ifYouAreNotSureWhy", new Object[]{emailProperties.getSupportEmail()}, currentLocale));
    }

    protected enum MailType {
        REGISTER, FORGOT, INVITE
    }
}
