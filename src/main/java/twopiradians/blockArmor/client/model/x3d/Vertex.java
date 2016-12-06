package twopiradians.blockArmor.client.model.x3d;

public class Vertex
{
    public float x, y, z;

    public Vertex(float x, float y)
    {
        this(x, y, 0F);
    }

    public Vertex(float x, float y, float z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
