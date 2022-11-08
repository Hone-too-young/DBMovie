package com.hdy.dbmovie.common.serializer;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.emoji.EmojiUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

@Component
public class EmojiJsonSerializer extends JsonSerializer<String> {
    @Override
    @SneakyThrows
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) {
        gen.writeString(EmojiUtil.toUnicode(StrUtil.isBlank(value)? "" : value));
    }
}
