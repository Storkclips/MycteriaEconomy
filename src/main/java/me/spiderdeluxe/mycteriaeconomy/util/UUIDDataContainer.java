package me.spiderdeluxe.mycteriaeconomy.util;

import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;

import java.nio.ByteBuffer;
import java.util.UUID;

public class UUIDDataContainer implements PersistentDataType<byte[], UUID> {

    @Override
    public Class<byte[]> getPrimitiveType() {
        return byte[].class;
    }

    @Override
    public Class<UUID> getComplexType() {
        return UUID.class;
    }

    @Override
    public byte[] toPrimitive(final UUID complex, final PersistentDataAdapterContext context) {
        final ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[16]);
        byteBuffer.putLong(complex.getMostSignificantBits());
        byteBuffer.putLong(complex.getLeastSignificantBits());
        return byteBuffer.array();
    }

    @Override
    public UUID fromPrimitive(final byte[] primitive, final PersistentDataAdapterContext context) {
        final ByteBuffer byteBuffer = ByteBuffer.wrap(primitive);
        final long firstLong = byteBuffer.getLong();
        final long secondLong = byteBuffer.getLong();
        return new UUID(firstLong, secondLong);
    }
}
