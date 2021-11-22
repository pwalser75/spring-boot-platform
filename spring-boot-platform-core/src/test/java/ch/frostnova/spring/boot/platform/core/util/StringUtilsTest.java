package ch.frostnova.spring.boot.platform.core.util;

import org.junit.jupiter.api.Test;

import java.util.Base64;

import static ch.frostnova.spring.boot.platform.core.util.StringUtils.md5;
import static ch.frostnova.spring.boot.platform.core.util.StringUtils.md5Base64;
import static ch.frostnova.spring.boot.platform.core.util.StringUtils.sha256;
import static ch.frostnova.spring.boot.platform.core.util.StringUtils.sha256Base64;
import static ch.frostnova.spring.boot.platform.core.util.StringUtils.toHex;
import static org.assertj.core.api.Assertions.assertThat;

public class StringUtilsTest {

    @Test
    void shouldCalculateMD5() {
        String text = "Lorem ipsum dolor sit amet";
        assertThat(md5(text)).isEqualTo(new byte[]{
                -2, -88, 15, 45, -80, 3, -44, -21, -60, 83, 96, 35, -127, 74, -88, -123
        });
        assertThat(md5Base64(text)).isEqualTo("/qgPLbAD1OvEU2AjgUqohQ==");
        assertThat(Base64.getEncoder().encodeToString(md5(text)))
                .isEqualTo("/qgPLbAD1OvEU2AjgUqohQ==");
    }

    @Test
    void shouldCalculateSHA256() {
        String text = "Lorem ipsum dolor sit amet";
        assertThat(sha256(text)).isEqualTo(new byte[]{
                22, -85, -91, 57, 58, -41, 44, 0, 65, -11, 96, 10, -45, -62, -59, 46, -60,
                55, -94, -16, -57, -4, 8, -6, -33, -61, -64, -2, -106, 65, -41, -93
        });
        assertThat(sha256Base64(text)).isEqualTo("FqulOTrXLABB9WAK08LFLsQ3ovDH/Aj638PA/pZB16M=");
        assertThat(Base64.getEncoder().encodeToString(sha256(text)))
                .isEqualTo("FqulOTrXLABB9WAK08LFLsQ3ovDH/Aj638PA/pZB16M=");
    }

    @Test
    void shouldConvertToHex() {
        byte[] data = {
                22, -85, -91, 57, 58, -41, 44, 0, 65, -11, 96, 10, -45, -62, -59, 46, -60,
                55, -94, -16, -57, -4, 8, -6, -33, -61, -64, -2, -106, 65, -41, -93
        };
        assertThat(toHex(data)).isEqualTo("16aba5393ad72c0041f5600ad3c2c52ec437a2f0c7fc08fadfc3c0fe9641d7a3");
    }
}
