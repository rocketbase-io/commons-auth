package io.rocketbase.commons.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;


@Data
@ConfigurationProperties(prefix = "auth.gravatar")
public class GravatarProperties {

    private boolean enabled = true;
    private Integer size = 160;
    private DefaultImage image = DefaultImage.RETRO;
    private ImageRating rating = null;

    public enum DefaultImage {
        /**
         * do not load any image if none is associated with the email hash, instead return an HTTP 404 (File Not Found) response
         */
        NOT_FOUND,
        /**
         * (mystery-man) a simple, cartoon-style silhouetted outline of a person (does not vary by email hash)
         */
        MM,
        /**
         * a geometric pattern based on an email hash
         */
        IDENTICON,
        /**
         * a generated 'monster' with different colors, faces, etc
         */
        MONSTERID,
        /**
         * generated faces with differing features and backgrounds
         */
        WAVATAR,
        /**
         * awesome generated, 8-bit arcade-style pixelated faces
         */
        RETRO,
        /**
         * a generated robot with different colors, faces, etc
         */
        ROBOHASH,
        /**
         * a transparent PNG image (border added to HTML below for demonstration purposes)
         */
        BLANK;

        public String getUrlParam() {
            return NOT_FOUND.equals(this) ? "404" : this.name().toLowerCase();
        }
    }

    public enum ImageRating {
        /**
         * suitable for display on all websites with any audience type.
         */
        G,
        /**
         * may contain rude gestures, provocatively dressed individuals, the lesser swear words, or mild violence.
         */
        PG,
        /**
         * may contain such things as harsh profanity, intense violence, nudity, or hard drug use.
         */
        R,
        /**
         * may contain hardcore sexual imagery or extremely disturbing violence.
         */
        X;

        public String getUrlParam() {
            return this.name().toLowerCase();
        }
    }

}
