package twopiradians.blockArmor.client.model.x3d;

import org.lwjgl.opengl.GL11;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;

public class Vector4
{
    public float x, y, z, w;

    public Vector4()
    {
        y = z = w = 0;
        x = 1;
    }

    public Vector4(double posX, double posY, double posZ, float w)
    {
        this.x = (float) posX;
        this.y = (float) posY;
        this.z = (float) posZ;
        this.w = w;
    }

    public Vector4(Entity e)
    {
        this(e.posX, e.posY, e.posZ, e.dimension);
    }

    public Vector4(NBTTagCompound nbt)
    {
        this();
        x = nbt.getFloat("x");
        y = nbt.getFloat("y");
        z = nbt.getFloat("z");
        w = nbt.getFloat("w");
    }

    public Vector4(String toParse)
    {
        String[] vals = toParse.split(" ");
        if (vals.length == 4)
        {
            this.x = Float.parseFloat(vals[0]);
            this.y = Float.parseFloat(vals[1]);
            this.z = Float.parseFloat(vals[2]);
            this.w = Float.parseFloat(vals[3]);
        }
    }

    public Vector4 add(Vector4 b)
    {
        Vector4 quat = new Vector4();

        quat.w = w + b.w;
        quat.x = x + b.x;
        quat.y = y + b.y;
        quat.z = z + b.z;

        return quat;
    }

    public Vector4 addAngles(Vector4 toAdd)
    {
        Vector4 ret = copy();
        Vector4 temp = toAdd.copy();

        if (Float.isNaN(temp.x) || Float.isNaN(temp.y) || Float.isNaN(temp.z) || Float.isNaN(temp.w))
        {
            System.out.println(temp + " " + toAdd);
            new Exception().printStackTrace();
            temp.x = 0;
            temp.y = 0;
            temp.z = 1;
            temp.w = 0;
        }
        temp.toQuaternion();
        ret.toQuaternion();
        ret.mul(ret.copy(), temp);
        return ret.toAxisAngle();
    }

    public Vector4 copy()
    {
        return new Vector4(x, y, z, w);
    }

    @Override
    public boolean equals(Object o)
    {
        if (o instanceof Vector4)
        {
            Vector4 v = (Vector4) o;
            return v.x == x && v.y == y && v.z == z && v.w == w;
        }

        return super.equals(o);
    }

    public void glRotate()
    {
        GL11.glRotatef(w, x, y, z);
    }

    public void glRotateMinus()
    {
        GL11.glRotatef(-w, x, y, z);
    }

    public boolean isEmpty()
    {
        return x == 0 && z == 0 && y == 0;
    }

    public final void mul(Vector4 q1, Vector4 q2)
    {
        x = q1.x * q2.w + q1.y * q2.z - q1.z * q2.y + q1.w * q2.x;
        y = -q1.x * q2.z + q1.y * q2.w + q1.z * q2.x + q1.w * q2.y;
        z = q1.x * q2.y - q1.y * q2.x + q1.z * q2.w + q1.w * q2.z;
        w = -q1.x * q2.x - q1.y * q2.y - q1.z * q2.z + q1.w * q2.w;
    }

    public Vector4 normalize()
    {
        float s = x * x + y * y + z * z + w * w;
        s = (float) Math.sqrt(s);
        x /= s;
        y /= s;
        z /= s;
        w /= s;

        return this;
    }

    public Vector4 scalarMult(float scalar)
    {
        Vector4 ret = new Vector4(x, y, z, w);
        ret.w = w * scalar;
        return ret;
    }

    public Vector4 set(float x, float y, float z, float w)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
        return this;
    }

    public Vector4 subtractAngles(Vector4 toAdd)
    {
        Vector4 temp = new Vector4(toAdd.x, toAdd.y, toAdd.z, -toAdd.w);
        return addAngles(temp);
    }

    /** The default is axis angle for use with openGL
     * 
     * @return */
    public Vector4 toAxisAngle()
    {
        float qw = w;
        float qx = x;
        float qy = y;
        float qz = z;

        if (w == 0)
        {
            x = 1;
            y = 0;
            z = 0;

            return this;
        }

        w = (float) Math.toDegrees((2 * Math.acos(qw)));
        float s = (float) Math.sqrt(1 - qw * qw);

        if (s == 0)
        {
            // System.err.println("Error "+this);
            // new Exception().printStackTrace();
        }

        if (s > 0.001f)
        {
            x = qx / s;
            y = qy / s;
            z = qz / s;
        }
        float rad = (float) Math.sqrt(x * x + y * y + z * z);

        x = x / rad;
        y = y / rad;
        z = z / rad;

        return this;
    }

    public String toIntString()
    {
        return "x:" + MathHelper.floor_double(x) + " y:" + MathHelper.floor_double(y) + " z:"
                + MathHelper.floor_double(z) + " w:" + MathHelper.floor_double(w);
    }

    public Vector4 toQuaternion()
    {
        double a = Math.toRadians(w);
        float ax = x;
        float ay = y;
        float az = z;

        this.w = (float) Math.cos(a / 2);
        this.x = (float) (ax * Math.sin(a / 2));
        this.y = (float) (ay * Math.sin(a / 2));
        this.z = (float) (az * Math.sin(a / 2));

        return this.normalize();
    }

    @Override
    public String toString()
    {
        return "x:" + x + " y:" + y + " z:" + z + " w:" + w;
    }

    public boolean withinDistance(float distance, Vector4 toCheck)
    {
        if ((int) w == (int) toCheck.w && toCheck.x >= x - distance && toCheck.z >= z - distance
                && toCheck.y >= y - distance && toCheck.y <= y + distance && toCheck.x <= x + distance
                && toCheck.z <= z + distance) { return true; }

        return false;
    }

    public void writeToNBT(NBTTagCompound nbt)
    {
        nbt.setFloat("x", x);
        nbt.setFloat("y", y);
        nbt.setFloat("z", z);
        nbt.setFloat("w", w);
    }
}
