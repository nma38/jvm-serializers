// automatically generated, do not modify

package serializer.flatbuffers.media;

import java.nio.*;
import java.lang.*;
import java.util.*;
import com.google.flatbuffers.*;

@SuppressWarnings("unused")
public final class MediaContent extends Table {
  public static MediaContent getRootAsMediaContent(ByteBuffer _bb) { return getRootAsMediaContent(_bb, new MediaContent()); }
  public static MediaContent getRootAsMediaContent(ByteBuffer _bb, MediaContent obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__init(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public MediaContent __init(int _i, ByteBuffer _bb) { bb_pos = _i; bb = _bb; return this; }

  public Image image(int j) { return image(new Image(), j); }
  public Image image(Image obj, int j) { int o = __offset(4); return o != 0 ? obj.__init(__indirect(__vector(o) + j * 4), bb) : null; }
  public int imageLength() { int o = __offset(4); return o != 0 ? __vector_len(o) : 0; }
  public Media media() { return media(new Media()); }
  public Media media(Media obj) { int o = __offset(6); return o != 0 ? obj.__init(__indirect(o + bb_pos), bb) : null; }

  public static int createMediaContent(FlatBufferBuilder builder,
      int image,
      int media) {
    builder.startObject(2);
    MediaContent.addMedia(builder, media);
    MediaContent.addImage(builder, image);
    return MediaContent.endMediaContent(builder);
  }

  public static void startMediaContent(FlatBufferBuilder builder) { builder.startObject(2); }
  public static void addImage(FlatBufferBuilder builder, int imageOffset) { builder.addOffset(0, imageOffset, 0); }
  public static int createImageVector(FlatBufferBuilder builder, int[] data) { builder.startVector(4, data.length, 4); for (int i = data.length - 1; i >= 0; i--) builder.addOffset(data[i]); return builder.endVector(); }
  public static void startImageVector(FlatBufferBuilder builder, int numElems) { builder.startVector(4, numElems, 4); }
  public static void addMedia(FlatBufferBuilder builder, int mediaOffset) { builder.addOffset(1, mediaOffset, 0); }
  public static int endMediaContent(FlatBufferBuilder builder) {
    int o = builder.endObject();
    return o;
  }
  public static void finishMediaContentBuffer(FlatBufferBuilder builder, int offset) { builder.finish(offset); }
};

