package twopiradians.blockArmor.client.render.x3d;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.vecmath.Vector3f;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import twopiradians.blockArmor.client.render.model.Vertex;

public class X3dXML
{
    @XmlRootElement(name = "Appearance")
    public static class Appearance
    {
        @XmlElement(name = "ImageTexture")
        ImageTexture     tex;
        @XmlElement(name = "Material")
        Material         material;
        @XmlElement(name = "TextureTransform")
        TextureTransform texTransform;
    }

    @XmlRootElement(name = "Group")
    public static class Group
    {
        @XmlAttribute(name = "DEF")
        String      DEF;
        @XmlElement(name = "Shape")
        List<Shape> shapes = Lists.newArrayList();
    }

    @XmlRootElement(name = "ImageTexture")
    public static class ImageTexture
    {
        @XmlAttribute(name = "DEF")
        String DEF;
    }

    @XmlRootElement(name = "IndexedTriangleSet")
    public static class IndexedTriangleSet
    {
        @XmlRootElement(name = "Coordinate")
        public static class Coordinate
        {
            @XmlAttribute(name = "point")
            String point;

            public Vertex[] getVertices()
            {
                return parseVertices(point);
            }
        }

        @XmlRootElement(name = "Normal")
        public static class Normal
        {
            @XmlAttribute(name = "vector")
            String vector;

            public Vertex[] getNormals()
            {
                return parseVertices(vector);
            }
        }

        @XmlRootElement(name = "TextureCoordinate")
        public static class TextureCoordinate
        {
            @XmlAttribute(name = "point")
            String point;

            public twopiradians.blockArmor.client.render.model.TextureCoordinate[] getTexture()
            {
                ArrayList<twopiradians.blockArmor.client.render.model.TextureCoordinate> ret = new ArrayList<twopiradians.blockArmor.client.render.model.TextureCoordinate>();
                String[] points = point.split(" ");
                if (points.length % 2 != 0) { throw new ModelFormatException(
                        "Invalid number of elements in the points string " + points.length); }
                for (int i = 0; i < points.length; i += 2)
                {
                	twopiradians.blockArmor.client.render.model.TextureCoordinate toAdd = new twopiradians.blockArmor.client.render.model.TextureCoordinate(
                            Float.parseFloat(points[i]), 1 - Float.parseFloat(points[i + 1]));
                    ret.add(toAdd);
                }
                return ret.toArray(new twopiradians.blockArmor.client.render.model.TextureCoordinate[ret.size()]);
            }
        }

        private static Vertex[] parseVertices(String line) throws ModelFormatException
        {
            ArrayList<Vertex> ret = new ArrayList<Vertex>();

            String[] points = line.split(" ");
            if (points.length
                    % 3 != 0) { throw new ModelFormatException("Invalid number of elements in the points string"); }
            for (int i = 0; i < points.length; i += 3)
            {
                Vertex toAdd = new Vertex(Float.parseFloat(points[i]), Float.parseFloat(points[i + 1]),
                        Float.parseFloat(points[i + 2]));
                ret.add(toAdd);
            }
            return ret.toArray(new Vertex[ret.size()]);
        }

        @XmlAttribute(name = "solid")
        boolean           solid;

        @XmlAttribute(name = "normalPerVertex")
        boolean           normalPerVertex;

        @XmlAttribute(name = "index")
        String            index;

        @XmlElement(name = "Coordinate")
        Coordinate        points;
        @XmlElement(name = "Normal")
        Normal            normals;
        @XmlElement(name = "TextureCoordinate")
        TextureCoordinate textures;

        public Vertex[] getNormals()
        {
            return normals.getNormals();
        }

        public Integer[] getOrder()
        {
            String[] offset = index.split(" ");
            Integer[] order = new Integer[offset.length];
            for (int i = 0; i < offset.length; i++)
            {
                String s1 = offset[i];
                order[i] = (Integer.parseInt(s1));
            }
            return order;
        }

        public twopiradians.blockArmor.client.render.model.TextureCoordinate[] getTexture()
        {
            return textures.getTexture();
        }

        public Vertex[] getVertices()
        {
            return points.getVertices();
        }
    }

    @XmlRootElement(name = "Material")
    public static class Material
    {
        @XmlAttribute(name = "DEF")
        String           DEF;
        @XmlAttribute(name = "USE")
        String           USE;
        @XmlAttribute(name = "diffuseColor")
        private String   diffuseColor;
        @XmlAttribute(name = "specularColor")
        private String   specularColor;
        @XmlAttribute(name = "emissiveColor")
        private String   emissiveColor;
        @XmlAttribute(name = "ambientIntensity")
        float            ambientIntensity;
        @XmlAttribute(name = "shininess")
        float            shininess;
        @XmlAttribute(name = "transparency")
        float            transparency;
        private Vector3f diffuse;

        private Vector3f specular;

        private Vector3f emissive;

        public Vector3f getDiffuse()
        {
            if (diffuse == null)
            {
                diffuse = fromString(diffuseColor);
            }
            return diffuse;
        }

        public Vector3f getEmissive()
        {
            if (emissive == null)
            {
                emissive = fromString(emissiveColor);
            }
            return emissive;
        }

        public Vector3f getSpecular()
        {
            if (specular == null)
            {
                specular = fromString(specularColor);
            }
            return specular;
        }
    }

    @XmlRootElement(name = "Scene")
    public static class Scene
    {
        @XmlElement(name = "Transform")
        List<Transform> transforms = Lists.newArrayList();
    }

    @XmlRootElement(name = "Shape")
    public static class Shape
    {
        @XmlElement(name = "Appearance")
        Appearance         appearance;
        @XmlElement(name = "IndexedTriangleSet")
        IndexedTriangleSet triangleSet;
    }

    @XmlRootElement(name = "TextureTransform")
    public static class TextureTransform
    {
        @XmlAttribute(name = "translation")
        String translation;
        @XmlAttribute(name = "scale")
        String scale;
        @XmlAttribute(name = "rotation")
        float  rotation;
    }

    @XmlRootElement(name = "Transform")
    public static class Transform
    {
        @XmlAttribute(name = "DEF")
        String          DEF;
        @XmlAttribute(name = "translation")
        String          translation;
        @XmlAttribute(name = "scale")
        String          scale;
        @XmlAttribute(name = "rotation")
        String          rotation;
        @XmlElement(name = "Transform")
        List<Transform> transforms = Lists.newArrayList();
        @XmlElement(name = "Group")
        Group           group;

        public Set<String> getChildNames()
        {
            Set<String> ret = Sets.newHashSet();
            for (Transform t : transforms)
            {
                if (t.getGroupName() != null) ret.add(t.getGroupName());
            }
            return ret;
        }

        public String getGroupName()
        {
            if (group == null && getIfsTransform() != this) { return getIfsTransform().getGroupName(); }
            if (group == null || group.DEF == null) return getIfsTransform().DEF.replace("_ifs_TRANSFORM", "");
            return group.DEF.substring("group_ME_".length());
        }

        public Transform getIfsTransform()
        {
            if (DEF.endsWith("ifs_TRANSFORM")) return this;

            for (Transform t : transforms)
            {
                if (t.DEF.equals(DEF.replace("_TRANSFORM", "_ifs_TRANSFORM"))) return t;
            }
            return null;
        }

        @Override
        public String toString()
        {
            return DEF + " " + transforms;
        }
    }

    @XmlRootElement(name = "X3D")
    public static class X3D
    {
        @XmlElement(name = "Scene")
        Scene scene;
    }

    private static Vector3f fromString(String vect)
    {
        if (vect == null) vect = "0 0 0";
        String[] var = vect.split(" ");
        return new Vector3f(Float.parseFloat(var[0]), Float.parseFloat(var[1]), Float.parseFloat(var[2]));
    }

    public X3D model;

    public X3dXML(InputStream stream) throws JAXBException
    {
        JAXBContext jc = JAXBContext.newInstance(X3D.class);
        try
        {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            spf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            XMLReader xmlReader = spf.newSAXParser().getXMLReader();
            xmlReader.setEntityResolver(new EntityResolver()
            {
                @Override
                public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException
                {
                    return new InputSource(new StringReader(""));
                }
            });
            InputSource inputSource = new InputSource(new InputStreamReader(stream));
            SAXSource source = new SAXSource(xmlReader, inputSource);
            Unmarshaller unmarshaller = jc.createUnmarshaller();
            model = (X3D) unmarshaller.unmarshal(source);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}