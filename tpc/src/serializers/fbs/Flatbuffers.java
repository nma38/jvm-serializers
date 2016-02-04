package serializers.fbs;

import com.google.flatbuffers.FlatBufferBuilder;
import data.media.MediaTransformer;
import serializer.flatbuffers.media.*;
import serializers.*;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author nickma
 * @version 1.0
 * @since 02/02/16
 */
public class Flatbuffers {
    public static void register(TestGroups groups) {
        groups.media.add(new Transformer(), new PBSerializer(),
                new SerFeatures(
                        SerFormat.BIN_CROSSLANG,
                        SerGraph.FLAT_TREE,
                        SerClass.MANUAL_OPT,
                        ""
                ));
    }

    static final class PBSerializer extends Serializer<MediaContent>
    {
        public String getName() { return "flatbuffers"; }

        @Override
        public MediaContent deserialize (byte[] array) throws Exception {
            return MediaContent.getRootAsMediaContent(ByteBuffer.wrap(array));
        }

        @Override
        public byte[] serialize(MediaContent content) {
            return content.getByteBuffer().array();
        }

        @Override
        public final void serializeItems(MediaContent[] items, OutputStream out0) throws IOException
        {
            DataOutputStream out = new DataOutputStream(out0);
            for (MediaContent item : items) {
                byte[] data = serialize(item);
                out.writeInt(data.length);
                out.write(data);
            }
            // should we write end marker (length of 0) or not? For now, omit it
            out.flush();
        }

        @Override
        public MediaContent[] deserializeItems(InputStream in0, int numberOfItems) throws Exception
        {
            DataInputStream in = new DataInputStream(in0);
            MediaContent[] result = new MediaContent[numberOfItems];
            for (int i = 0; i < numberOfItems; ++i) {
                int len = in.readInt();
                byte[] data = new byte[len];
                in.readFully(data);
                result[i] = deserialize(data);
            }
            return result;
        }
    }

    public static final void readFullyDirectly(FlatBufferBuilder fbsBuilder, InputStream in, int len) throws IOException {
        if (len < 0)
            throw new IndexOutOfBoundsException();
        int n = 0;
        while (n < len) {
            int count = in.read();
            fbsBuilder.addByte((byte) count);
            if (count < 0)
                throw new EOFException();
            n += count;
        }
    }

    public static final class Transformer extends MediaTransformer<MediaContent>
    {
        @Override
        public MediaContent[] resultArray(int size) { return new MediaContent[size]; }

        // ----------------------------------------------------------
        // Forward

        @Override
        public MediaContent forward(data.media.MediaContent mc)
        {
            FlatBufferBuilder builder = new FlatBufferBuilder();
            int mediaOffset = forwardMedia(mc.getMedia(), builder);
            int[] imageOffsets = new int[mc.images.size()];
            for (int i = 0; i < mc.images.size(); i++) {
                imageOffsets[i] = forwardImage(mc.images.get(i), builder);
            }

            int rootTableOffset = MediaContent.createMediaContent(builder, MediaContent.createImageVector(builder, imageOffsets), mediaOffset);
            builder.finish(rootTableOffset);
            return MediaContent.getRootAsMediaContent(builder.dataBuffer());
        }

        private int forwardMedia(data.media.Media media, FlatBufferBuilder builder)
        {
            int[] personVectorOffsets = new int[media.persons.size()];
            for (int i = 0; i < media.persons.size(); i++) {
                personVectorOffsets[i] = builder.createString(media.persons.get(i));
            }
            // Media
            int mediaOffset = Media.createMedia(builder,
                    builder.createString(media.uri),
                    media.title != null ? builder.createString(media.title) : builder.createString(""),
                    media.width,
                    media.height,
                    builder.createString(media.format),
                    media.duration,
                    media.size,
                    media.bitrate,
                    Media.createPersonVector(builder, personVectorOffsets),
                    forwardPlayer(media.player),
                    media.copyright != null ? builder.createString(media.copyright) : builder.createString("")
            );
            return mediaOffset;
        }

        public Byte forwardPlayer(data.media.Media.Player p)
        {
            switch (p) {
                case JAVA: return Player.JAVA;
                case FLASH: return Player.FLASH;
                default:
                    throw new AssertionError("invalid case: " + p);
            }
        }

        private int forwardImage(data.media.Image image, FlatBufferBuilder builder)
        {
            int imageOffset = Image.createImage(builder,
                    builder.createString(image.uri),
                    image.title != null ? builder.createString(image.title) : builder.createString(""),
                    image.width,
                    image.height,
                    forwardSize(image.size)
                    );

            return imageOffset;
        }

        public Byte forwardSize(data.media.Image.Size s)
        {
            switch (s) {
                case SMALL: return Size.SMALL;
                case LARGE: return Size.LARGE;
                default:
                    throw new AssertionError("invalid case: " + s);
            }
        }

        // ----------------------------------------------------------
        // Reverse

        @Override
        public data.media.MediaContent reverse(MediaContent mc)
        {
            List<data.media.Image> images = new ArrayList<>(mc.imageLength());

            for (int i = 0; i < mc.imageLength(); i++) {
                images.add(reverseImage(mc.image(i)));
            }

            if (mc.media() == null) {
                throw new RuntimeException();
            }

            data.media.Media media = reverseMedia(mc.media());
            return new data.media.MediaContent(media, images);
        }

        private data.media.Media reverseMedia(Media media)
        {
            ArrayList<String> persons = new ArrayList<>();
            for (int i = 0; i < media.personLength(); i++) {
                persons.add(media.person(i));
            }
            // Media
            return new data.media.Media(
                    media.uri(),
                    media.title(),
                    media.width(),
                    media.height(),
                    media.format(),
                    media.duration(),
                    media.size(),
                    media.bitrate(),
                    media.bitrate() > 1 ? true:false,
                    persons,
                    reversePlayer(media.player()),
                    media.copyright()
            );
        }

        public data.media.Media.Player reversePlayer(Byte p)
        {
            if (p == Player.JAVA) return data.media.Media.Player.JAVA;
            if (p == Player.FLASH) return data.media.Media.Player.FLASH;
            throw new AssertionError("invalid case: " + p);
        }

        private data.media.Image reverseImage(Image image)
        {
            return new data.media.Image(
                    image.uri(),
                    image.title(),
                    image.width(),
                    image.height(),
                    reverseSize(image.size()));
        }

        public data.media.Image.Size reverseSize(Byte s)
        {
            switch (s) {
                case Size.SMALL: return data.media.Image.Size.SMALL;
                case Size.LARGE: return data.media.Image.Size.LARGE;
                default:
                    throw new AssertionError("invalid case: " + s);
            }
        }

        public data.media.MediaContent shallowReverse(MediaContent mc)
        {
            return new data.media.MediaContent(reverseMedia(mc.media()), Collections.<data.media.Image>emptyList());
        }
    };
}
