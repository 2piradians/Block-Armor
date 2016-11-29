package twopiradians.blockArmor.client.render.model;

import java.util.HashMap;

import javax.vecmath.Vector3f;

public interface IExtendedModelPart extends IModelCustom
{
    void addChild(IExtendedModelPart child);

    Vector4 getDefaultRotations();

    Vector3f getDefaultTranslations();

    String getName();

    IExtendedModelPart getParent();

    int[] getRGBAB();

    HashMap<String, IExtendedModelPart> getSubParts();

    String getType();

    void resetToInit();

    void setParent(IExtendedModelPart parent);

    void setPostRotations(Vector4 rotations);

    void setPostRotations2(Vector4 rotations);

    void setPostTranslations(Vector3f translations);

    void setPreRotations(Vector4 rotations);

    void setPreTranslations(Vector3f translations);

    void setPreScale(Vector3f scale);

    void setRGBAB(int[] arrays);
}
