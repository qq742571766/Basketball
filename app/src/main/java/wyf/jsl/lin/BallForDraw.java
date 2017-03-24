package wyf.jsl.lin;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

public class BallForDraw {
    private FloatBuffer myVertex;
    private FloatBuffer myTexture;

    public int xAngle;
    public int yAngle;
    public int zAngle;

    int vcount;
    int textureID;

    public BallForDraw(float angleSpan, float scale, int textureID) {
        this.textureID = textureID;
        ArrayList<Float> val = new ArrayList<Float>();
        for (float i = 90; i > -90; i -= angleSpan) {
            for (float j = 360; j > 0; j -= angleSpan) {
                float x1 = (float) (scale * Math.cos(Math.toRadians(i)) * Math.cos(Math.toRadians(j)));
                float z1 = (float) (scale * Math.sin(Math.toRadians(i)));
                float y1 = (float) (scale * Math.cos(Math.toRadians(i)) * Math.sin(Math.toRadians(j)));

                float x2 = (float) (scale * Math.cos(Math.toRadians(i - angleSpan)) * Math.cos(Math.toRadians(j)));
                float z2 = (float) (scale * Math.sin(Math.toRadians(i - angleSpan)));
                float y2 = (float) (scale * Math.cos(Math.toRadians(i - angleSpan)) * Math.sin(Math.toRadians(j)));

                float x3 = (float) (scale * Math.cos(Math.toRadians(i - angleSpan)) * Math.cos(Math.toRadians(j - angleSpan)));
                float z3 = (float) (scale * Math.sin(Math.toRadians(i - angleSpan)));
                float y3 = (float) (scale * Math.cos(Math.toRadians(i - angleSpan)) * Math.sin(Math.toRadians(j - angleSpan)));

                float x4 = (float) (scale * Math.cos(Math.toRadians(i)) * Math.cos(Math.toRadians(j - angleSpan)));
                float z4 = (float) (scale * Math.sin(Math.toRadians(i)));
                float y4 = (float) (scale * Math.cos(Math.toRadians(i)) * Math.sin(Math.toRadians(j - angleSpan)));

                val.add(x1);
                val.add(y1);
                val.add(z1);
                val.add(x2);
                val.add(y2);
                val.add(z2);
                val.add(x4);
                val.add(y4);
                val.add(z4);

                val.add(x4);
                val.add(y4);
                val.add(z4);
                val.add(x2);
                val.add(y2);
                val.add(z2);
                val.add(x3);
                val.add(y3);
                val.add(z3);
            }
        }
        vcount = val.size() / 3;
        float[] ver = new float[vcount * 3];
        for (int i = 0; i < vcount * 3; i++) {
            ver[i] = val.get(i);
        }
        ByteBuffer vbb = ByteBuffer.allocateDirect(ver.length * 4);
        vbb.order(ByteOrder.nativeOrder());
        myVertex = vbb.asFloatBuffer();
        myVertex.put(ver);
        myVertex.position(0);

        ArrayList<Float> tal = new ArrayList<Float>();
        int trow = (int) (180 / angleSpan);
        int tcol = (int) (360 / angleSpan);
        float rowUnitSize = (float) 1.0f / trow;
        float colUnitSize = (float) 1.0f / tcol;
        for (int i = 0; i < trow; i++) {
            for (int j = 0; j < tcol; j++) {
                tal.add(j * colUnitSize);
                tal.add(i * rowUnitSize);

                tal.add(j * colUnitSize);
                tal.add(i * rowUnitSize + rowUnitSize);

                tal.add(j * colUnitSize + colUnitSize);
                tal.add(i * rowUnitSize);

                tal.add(j * colUnitSize + colUnitSize);
                tal.add(i * rowUnitSize);

                tal.add(j * colUnitSize);
                tal.add(i * rowUnitSize + rowUnitSize);

                tal.add(j * colUnitSize + colUnitSize);
                tal.add(i * rowUnitSize + rowUnitSize);
            }
        }
        float[] textures = new float[trow * tcol * 6 * 2];
        for (int i = 0; i < textures.length; i++) {
            textures[i] = tal.get(i);
        }

        ByteBuffer tbb = ByteBuffer.allocateDirect(textures.length * 4);
        tbb.order(ByteOrder.nativeOrder());
        myTexture = tbb.asFloatBuffer();
        myTexture.put(textures);
        myTexture.position(0);
    }

    public void drawSelf(GL10 gl) {
        gl.glRotatef(xAngle, 1, 0, 0);
        gl.glRotatef(60, 0, 1, 0);
        gl.glRotatef(zAngle, 0, 0, 1);

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, myVertex);

        gl.glEnable(GL10.GL_TEXTURE_2D);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, myTexture);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textureID);

        gl.glDrawArrays(GL10.GL_TRIANGLES, 0, vcount);
    }
}
