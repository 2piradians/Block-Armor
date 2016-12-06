package twopiradians.blockArmor.client.model.x3d;

import java.util.HashMap;
import java.util.List;

import javax.vecmath.Vector3f;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;

public class X3dObject implements IExtendedModelPart, IRetexturableModel
{
    public int                                 GLMODE     = GL11.GL_TRIANGLES;

    public List<Shape>                         shapes     = Lists.newArrayList();

    public HashMap<String, IExtendedModelPart> childParts = new HashMap<String, IExtendedModelPart>();
    public final String                        name;
    public IExtendedModelPart                  parent     = null;
    IPartTexturer                              texturer;
    IAnimationChanger                          changer;

    public Vector4                             preRot     = new Vector4();
    public Vector4                             postRot    = new Vector4();
    public Vector4                             postRot1   = new Vector4();
    public Vector3f                            preTrans   = new Vector3f();
    public Vector3f                            postTrans  = new Vector3f();
    public Vertex                              preScale   = new Vertex(1, 1, 1);

    public Vector3f                            offset     = new Vector3f();
    public Vector4                             rotations  = new Vector4();
    public Vertex                              scale      = new Vertex(1, 1, 1);

    public int                                 red        = 255, green = 255, blue = 255, alpha = 255;

    public int                                 brightness = 15728640;

    public X3dObject(String name)
    {
        this.name = name;
    }

    @Override
    public void addChild(IExtendedModelPart subPart)
    {
        this.childParts.put(subPart.getName(), subPart);
        subPart.setParent(this);
    }

    public void addForRender()
    {
        // Set colours.
        GL11.glColor4f(red / 255f, green / 255f, blue / 255f, alpha / 255f);
        // Render each Shape
        for (Shape s : shapes)
        {
            s.renderShape(texturer);
        }
    }

    @Override
    public Vector4 getDefaultRotations()
    {
        return rotations;
    }

    @Override
    public Vector3f getDefaultTranslations()
    {
        return offset;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public IExtendedModelPart getParent()
    {
        return parent;
    }

    @Override
    public int[] getRGBAB()
    {
        return new int[] { red, green, blue, alpha, brightness };
    }

    @Override
    public HashMap<String, IExtendedModelPart> getSubParts()
    {
        return childParts;
    }

    @Override
    public String getType()
    {
        return "x3d";
    }

    public void render()
    {
        // Rotate to the offset of the parent.
        rotateToParent();
        // Translate of offset for rotation.
        GL11.glTranslated(offset.x, offset.y, offset.z);
        // Rotate by this to account for a coordinate difference.
        GL11.glRotatef(90, 1, 0, 0);
        GL11.glTranslated(preTrans.x, preTrans.y, preTrans.z);
        // UnRotate coordinate difference.
        GL11.glRotatef(-90, 1, 0, 0);
        // Apply initial part rotation
        rotations.glRotate();
        // Rotate by this to account for a coordinate difference.
        GL11.glRotatef(90, 1, 0, 0);
        // Apply PreOffset-Rotations.
        preRot.glRotate();
        // Translate by post-PreOffset amount.
        GL11.glTranslated(postTrans.x, postTrans.y, postTrans.z);
        // UnRotate coordinate difference.
        GL11.glRotatef(-90, 1, 0, 0);
        // Undo pre-translate offset.
        GL11.glTranslated(-offset.x, -offset.y, -offset.z);
        GL11.glPushMatrix();
        // Translate to Offset.
        GL11.glTranslated(offset.x, offset.y, offset.z);

        // Apply first postRotation
        postRot.glRotate();
        // Apply second post rotation.
        postRot1.glRotate();
        // Scale
        GL11.glScalef(scale.x, scale.y, scale.z);
        // Renders the model.
        addForRender();
        GL11.glPopMatrix();
    }

    @Override
    public void renderAll()
    {
        GL11.glScalef(preScale.x, preScale.y, preScale.z);
        render();
        for (IExtendedModelPart o : childParts.values())
        {
            GL11.glPushMatrix();
            GL11.glTranslated(offset.x, offset.y, offset.z);
            GL11.glScalef(scale.x, scale.y, scale.z);
            o.renderAll();
            GL11.glPopMatrix();
        }
    }

    @Override
    public void renderAllExcept(String... excludedGroupNames)
    {
        for (String s : childParts.keySet())
        {
            for (String s1 : excludedGroupNames)
                if (!s.equalsIgnoreCase(s1))
                {
                    childParts.get(s).renderAllExcept(excludedGroupNames);
                }
        }
        for (String s1 : excludedGroupNames)
            if (s1.equalsIgnoreCase(name)) render();
    }

    @Override
    public void renderOnly(String... groupNames)
    {
        for (IExtendedModelPart o : childParts.values())
        {
            for (String s : groupNames)
            {
                if (s.equalsIgnoreCase(o.getName()))
                {
                    o.renderOnly(groupNames);
                }
            }
        }
        for (String s : groupNames)
        {
            if (s.equalsIgnoreCase(name)) render();
        }
    }

    @Override
    public void renderPart(String partName)
    {
        if (this.name.equalsIgnoreCase(partName)) render();
        if (childParts.containsKey(partName)) childParts.get(partName).renderPart(partName);
    }

    @Override
    public void resetToInit()
    {
        preRot.set(0, 1, 0, 0);
        postRot.set(0, 1, 0, 0);
        postRot1.set(0, 1, 0, 0);
        preTrans.set(0, 0, 0);
        postTrans.set(0, 0, 0);
    }

    private void rotateToParent()
    {
        if (parent != null)
        {
            if (parent instanceof X3dObject)
            {
                X3dObject parent = ((X3dObject) this.parent);
                parent.postRot.glRotate();
                parent.postRot1.glRotate();
            }
        }
    }

    @Override
    public void setAnimationChanger(IAnimationChanger changer)
    {
        this.changer = changer;
        for (IExtendedModelPart part : childParts.values())
        {
            if (part instanceof IRetexturableModel) ((IRetexturableModel) part).setAnimationChanger(changer);
        }
    }

    @Override
    public void setParent(IExtendedModelPart parent)
    {
        this.parent = parent;
    }

    @Override
    public void setPostRotations(Vector4 angles)
    {
        postRot = angles;
    }

    @Override
    public void setPostRotations2(Vector4 rotations)
    {
        postRot1 = rotations;
    }

    @Override
    public void setPostTranslations(Vector3f point)
    {
        postTrans.set(point);
    }

    @Override
    public void setPreRotations(Vector4 angles)
    {
        preRot = angles;
    }

    @Override
    public void setPreTranslations(Vector3f point)
    {
        preTrans.set(point);
    }

    @Override
    public void setRGBAB(int[] array)
    {
        red = array[0];
        blue = array[1];
        green = array[2];
        alpha = array[3];
        brightness = array[4];
    }

    @Override
    public void setTexturer(IPartTexturer texturer)
    {
        this.texturer = texturer;
        for (IExtendedModelPart part : childParts.values())
        {
            if (part instanceof IRetexturableModel) ((IRetexturableModel) part).setTexturer(texturer);
        }
    }

    @Override
    public void setPreScale(Vector3f scale)
    {
        preScale.x = (float) scale.x;
        preScale.y = (float) scale.y;
        preScale.z = (float) scale.z;
    }
}
