package io.rocketbase.commons.service.email;

import io.rocketbase.commons.config.EmailProperties;
import io.rocketbase.commons.model.AppInviteEntity;
import io.rocketbase.commons.model.AppUserReference;
import io.rocketbase.mail.EmailTemplateBuilder;
import io.rocketbase.mail.EmailTemplateBuilder.EmailTemplateConfigBuilder;
import io.rocketbase.mail.Header;
import io.rocketbase.mail.model.HtmlTextEmail;
import io.rocketbase.mail.styling.ColorStyleSimple;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.util.Pair;
import org.springframework.util.StringUtils;

import java.util.Locale;

import static io.rocketbase.commons.service.email.DefaultMailContentConfig.MailType.*;

@RequiredArgsConstructor
public class DefaultMailContentConfig implements MailContentConfig {

    final EmailProperties emailProperties;
    final MessageSource messageSource;

    private static Pair<String, String> convert(HtmlTextEmail htmlTextEmail) {
        return Pair.of(htmlTextEmail.getHtml(), htmlTextEmail.getText());
    }

    @Override
    public Pair<String, String> register(AppUserReference user, String actionUrl) {
        Locale currentLocale = LocaleContextHolder.getLocale();
        EmailTemplateConfigBuilder builder = EmailTemplateBuilder.builder();
        // header
        buildHeader(REGISTER, builder);
        // title
        buildTitle(REGISTER, builder, messageSource.getMessage("auth.email.register.header", new Object[]{}, currentLocale));
        // intro text
        builder.text(messageSource.getMessage("auth.email.register.hi", new Object[]{user.getUsername()}, currentLocale)).and()
                .text(messageSource.getMessage("auth.email.register.pleaseVerifyAccount", new Object[]{}, currentLocale));
        // button
        buildButton(REGISTER, builder, messageSource.getMessage("auth.email.register.button", new Object[]{}, currentLocale), actionUrl);
        // greeting / footer...
        return convert(builder
                .text(getGreeting()).and()
                .footerText(getFooter(currentLocale)).center().and()
                .copyright(emailProperties.getCopyrightName()).url(emailProperties.getCopyrightUrl())
                .build());
    }

    @Override
    public String registerSubject(AppUserReference user) {
        Locale currentLocale = LocaleContextHolder.getLocale();
        return messageSource.getMessage("auth.email.register.subject", new Object[]{emailProperties.getSubjectPrefix()}, currentLocale).trim();
    }

    @Override
    public Pair<String, String> forgotPassword(AppUserReference user, String actionUrl) {
        Locale currentLocale = LocaleContextHolder.getLocale();
        EmailTemplateConfigBuilder builder = EmailTemplateBuilder.builder();
        // header
        buildHeader(FORGOT, builder);
        // title
        buildTitle(FORGOT, builder, messageSource.getMessage("auth.email.forgot.header", new Object[]{}, currentLocale));
        // intro text
        builder.text(messageSource.getMessage("auth.email.forgot.hi", new Object[]{user.getUsername()}, currentLocale)).and()
                .text(messageSource.getMessage("auth.email.forgot.confirmPasswordChange", new Object[]{}, currentLocale));
        // button
        buildButton(FORGOT, builder, messageSource.getMessage("auth.email.forgot.button", new Object[]{}, currentLocale), actionUrl);
        // greeting / footer...
        return convert(builder.text(getGreeting()).and()
                .footerText(getFooter(currentLocale)).center().and()
                .copyright(emailProperties.getCopyrightName()).url(emailProperties.getCopyrightUrl())
                .build());
    }

    @Override
    public String forgotPasswordSubject(AppUserReference user) {
        Locale currentLocale = LocaleContextHolder.getLocale();
        return messageSource.getMessage("auth.email.forgot.subject", new Object[]{emailProperties.getSubjectPrefix()}, currentLocale).trim();
    }

    @Override
    public Pair<String, String> invite(AppInviteEntity invite, String actionUrl) {
        Locale currentLocale = LocaleContextHolder.getLocale();
        EmailTemplateConfigBuilder builder = EmailTemplateBuilder.builder();
        // header
        buildHeader(INVITE, builder);
        // title
        buildTitle(INVITE, builder, messageSource.getMessage("auth.email.invite.header", new Object[]{}, currentLocale));
        // intro text
        builder.text(messageSource.getMessage("auth.email.invite.welcome", new Object[]{invite.getDisplayName()}, currentLocale)).and()
                .text(messageSource.getMessage("auth.email.invite.youHaveInvitedBy", new Object[]{invite.getInvitor(), getServiceName()}, currentLocale)).and();
        // invite message
        if (!StringUtils.isEmpty(invite.getMessage())) {
            builder.text("").and()
                    .text(messageSource.getMessage("auth.email.invite.messageFrom", new Object[]{invite.getInvitor()}, currentLocale)).center().italic().and()
                    .text(invite.getMessage()).center().italic().bold().and()
                    .text("");
        }
        builder.text(messageSource.getMessage("auth.email.invite.createAccount", new Object[]{}, currentLocale));
        // button
        buildButton(INVITE, builder, messageSource.getMessage("auth.email.invite.button", new Object[]{}, currentLocale), actionUrl);
        // greeting / footer...
        return convert(builder.text(getGreeting()).and()
                .footerText(getFooterInvite(invite, currentLocale)).center().and()
                .copyright(emailProperties.getCopyrightName()).url(emailProperties.getCopyrightUrl())
                .build());
    }

    @Override
    public String inviteSubject(AppInviteEntity invite) {
        Locale currentLocale = LocaleContextHolder.getLocale();
        return messageSource.getMessage("auth.email.invite.subject", new Object[]{emailProperties.getSubjectPrefix(), invite.getInvitor()}, currentLocale).trim();
    }

    @Override
    public Pair<String, String> changeEmail(AppUserReference user, String newEmailAddress, String actionUrl) {
        Locale currentLocale = LocaleContextHolder.getLocale();
        EmailTemplateConfigBuilder builder = EmailTemplateBuilder.builder();
        // header
        buildHeader(CHANGE_EMAIL, builder);
        // title
        buildTitle(CHANGE_EMAIL, builder, messageSource.getMessage("auth.email.emailChange.header", new Object[]{}, currentLocale));
        // intro text
        builder.text(messageSource.getMessage("auth.email.emailChange.hi", new Object[]{user.getUsername()}, currentLocale)).and()
                .text(messageSource.getMessage("auth.email.emailChange.pleaseVerifyNewEmail", new Object[]{}, currentLocale));
        // button
        buildButton(CHANGE_EMAIL, builder, messageSource.getMessage("auth.email.emailChange.button", new Object[]{}, currentLocale), actionUrl);
        // greeting / footer...
        return convert(builder
                .text(getGreeting()).and()
                .footerText(getFooter(currentLocale)).center().and()
                .copyright(emailProperties.getCopyrightName()).url(emailProperties.getCopyrightUrl())
                .build());
    }

    @Override
    public String changeEmailSubject(AppUserReference user) {
        Locale currentLocale = LocaleContextHolder.getLocale();
        return messageSource.getMessage("auth.email.emailChange.subject", new Object[]{emailProperties.getSubjectPrefix()}, currentLocale).trim();
    }

    protected ColorStyleSimple getButtonColor(MailType type) {
        switch (type) {
            case REGISTER:
                return ColorStyleSimple.BLUE_STYLE;
            case FORGOT:
                return ColorStyleSimple.RED_STYLE;
            case INVITE:
                return ColorStyleSimple.GREEN_STYLE;
            case CHANGE_EMAIL:
                return ColorStyleSimple.YELLOW_STYLE;
        }
        return ColorStyleSimple.BASE_STYLE;
    }

    protected void buildHeader(MailType type, EmailTemplateConfigBuilder builder) {
        EmailProperties.EmailLogo logo = emailProperties.getLogo();
        if (logo != null && !StringUtils.isEmpty(logo.getSrc())) {
            Header header = builder.header().logo(logo.getSrc()).text(getServiceName());
            if (logo.getHeight() != null) {
                header.logoHeight(logo.getHeight());
            }
            if (logo.getWidth() != null) {
                header.logoWidth(logo.getWidth());
            }
        } else {
            builder.header().text(getServiceName());
        }
    }

    protected void buildTitle(MailType type, EmailTemplateConfigBuilder builder, String text) {
        builder.text(text).h1();
    }

    protected void buildButton(MailType type, EmailTemplateConfigBuilder builder, String text, String actionUrl) {
        builder.button(text, actionUrl).color(getButtonColor(type)).center();
        appendButton(type, builder);
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
        return String.format("%s\n%s", messageSource.getMessage("auth.email.greeting", new Object[]{}, LocaleContextHolder.getLocale()), emailProperties.getGreetingFrom());
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
        REGISTER, FORGOT, INVITE, CHANGE_EMAIL
    }
}
