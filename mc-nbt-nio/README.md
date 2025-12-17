# MC-NBT NIO

This module handles binary serialization. In the context of Minecraft, NBT data is often compressed (GZip for files like `level.dat`, or Zlib for Chunk data in Anvil files).

## Compression (GZip)

Since the NIO module works with `ByteBuffer`, you can use standard Java IO to handle compressed files.

```java
import java.io.FileInputStream;
import java.io.GZIPInputStream;
import java.nio.ByteBuffer;

public void readGzippedFile(File file) throws IOException {
try (FileInputStream fis = new FileInputStream(file);
    GZIPInputStream gzis = new GZIPInputStream(fis)) {

        byte[] bytes = gzis.readAllBytes();
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        
        NBTTagIdentifiable<?> result = NIOToNBT.readNamedTag(buffer);
        System.out.println("Loaded from GZip: " + result.name());
    }
}
```

## Netty Integration

In a Netty-based network stack (like a Minecraft Proxy or Server), you can wrap `NIOToNBT` and `NBTToNIO` into specialized handlers.

### NBT Encoder (Writing to Pipeline)
```java
public class NBTEncoder extends MessageToByteEncoder<NBTTagIdentifiable<?>> {
    @Override
    protected void encode(ChannelHandlerContext ctx, NBTTagIdentifiable<?> msg, ByteBuf out) {
        // 1. Calculate size
        int size = NBTSizeProvider.INSTANCE.calculateNamedTagSize(msg.name(), msg.tag());

        // 2. Ensure capacity and get NIO buffer
        out.ensureWritable(size);
        ByteBuffer nioBuffer = out.nioBuffer(out.writerIndex(), size);
        
        // 3. Serialize directly into the Netty ByteBuf memory
        NBTToNIO.writeNamedTag(nioBuffer, msg);
        
        // 4. Update Netty writer index
        out.writerIndex(out.writerIndex() + size);
    }
}
```

### NBT Decoder (Reading from Pipeline)
```java
public class NBTDecoder extends ReplayingDecoder<Void> {
@Override
protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        // Convert Netty ByteBuf to NIO ByteBuffer for the library
        ByteBuffer nioBuffer = in.nioBuffer(in.readerIndex(), in.readableBytes());

        // Read named tag
        NBTTagIdentifiable<?> result = NIOToNBT.readNamedTag(nioBuffer);
        
        // Advance Netty's reader index by the bytes read in NIO
        in.skipBytes(nioBuffer.position());
        
        out.add(result);
    }
}
```

## Design Choices for Networking
- **Zero-Copy**: By using `out.nioBuffer()`, we serialize directly into the memory allocated by Netty, avoiding intermediate byte-array copies.
- **Size Calculation**: `NBTSizeProvider` is critical here to tell Netty exactly how much memory to allocate before the writing process starts.
