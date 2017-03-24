package wyf.jsl.lin;

public class CollisionUtil {
    public static float dotProduct(float[] vec1, float[] vec2) {
        return
                vec1[0] * vec2[0] +
                        vec1[1] * vec2[1] +
                        vec1[2] * vec2[2];
    }

    public static float mould(float[] vec) {
        return (float) Math.sqrt(vec[0] * vec[0] + vec[1] * vec[1] + vec[2] * vec[2]);
    }

    public static float angle(float[] vec1, float[] vec2) {
        float dp = dotProduct(vec1, vec2);
        float m1 = mould(vec1);
        float m2 = mould(vec2);

        return (float) Math.acos(dp / (m1 * m2));
    }

    public static float[] ballBreak
            (
                    float[] vBefore,
                    float[] ballCenter,
                    float[] point
            ) {
        float[] vecOtoB =
                {
                        point[0] - ballCenter[0],
                        point[1] - ballCenter[1],
                        point[2] - ballCenter[2],
                };

        float alpha = angle(vecOtoB, vBefore);
        float vMould = mould(vBefore);
        float vNormalMould = vMould * (float) Math.cos(alpha);
        float normalMould = mould(vecOtoB);
        float[] vNormal =
                {
                        vecOtoB[0] * vNormalMould / normalMould,
                        vecOtoB[1] * vNormalMould / normalMould,
                        vecOtoB[2] * vNormalMould / normalMould,
                };

        float[] vONormal =
                {
                        vBefore[0] - vNormal[0],
                        vBefore[1] - vNormal[1],
                        vBefore[2] - vNormal[2]
                };

        float[] vNormalAfter =
                {
                        -vNormal[0],
                        -vNormal[1],
                        -vNormal[2]
                };

        float[] vAfter =
                {
                        vONormal[0] + vNormalAfter[0],
                        vONormal[1] + vNormalAfter[1],
                        vONormal[2] + vNormalAfter[2],
                };

        return vAfter;
    }

    public static float[] breakPoint
            (
                    float[] ballCenter,
                    float ballR,
                    float[] ringCenter,
                    float ringR
            ) {
        if (ringCenter[1] < ballCenter[1] + ballR && ringCenter[1] > ballCenter[1] - ballR)//ringCenter[1]Ϊ���ĸ߶ȣ�ballCenter[1]Ϊ���ĵĸ߶�
        {
            float ballRForBreak = (float) Math.sqrt
                    (
                            ballR * ballR -
                                    (ringCenter[1] - ballCenter[1]) * (ringCenter[1] - ballCenter[1])//ringCenter[1]-ballCenter[1]Ϊ�߶Ȳ�
                    );

            float distance = (float) Math.sqrt
                    (
                            (ballCenter[0] - ringCenter[0]) * (ballCenter[0] - ringCenter[0]) +
                                    (ballCenter[2] - ringCenter[2]) * (ballCenter[2] - ringCenter[2])
                    );

            if ((distance < ballRForBreak + ringR) && (distance > (ringR - ballRForBreak))) {
                float zDiff = Math.abs(ringCenter[2] - ballCenter[2]);
                double jdAngle = Math.asin(zDiff / distance);

                float xAdjust = 0;
                float zAdjust = 0;

                if (ballCenter[0] >= ringCenter[0]) {
                    xAdjust = -1;
                } else {
                    xAdjust = +1;
                }
                if (ballCenter[2] >= ringCenter[2]) {
                    zAdjust = -1;
                } else {
                    zAdjust = 1;
                }

                float[] point =
                        {
                                ballCenter[0] + xAdjust * ballRForBreak * (float) Math.cos(jdAngle),
                                ringCenter[1],
                                ballCenter[2] + zAdjust * ballRForBreak * (float) Math.sin(jdAngle),
                        };

                return point;
            } else {
                return null;
            }

        }

        return null;
    }


    public static void main(String args[]) {
        float[] vAfter = CollisionUtil.ballBreak
                (
                        new float[]{2, -2, 0},
                        new float[]{4, 4, 0},
                        new float[]{6.828427f, 1.1715729f, 0}
                );
        System.out.println(vAfter[0] + ", " + vAfter[1] + ", " + vAfter[2]);
    }
}