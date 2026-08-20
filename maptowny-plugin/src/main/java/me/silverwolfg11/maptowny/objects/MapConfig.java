/*
 * Copyright (c) 2021 Silverwolfg11
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.silverwolfg11.maptowny.objects;

import me.Silverwolfg11.CommentConfig.annotations.Comment;
import me.Silverwolfg11.CommentConfig.annotations.ConfigVersion;
import me.Silverwolfg11.CommentConfig.annotations.Node;
import me.Silverwolfg11.CommentConfig.annotations.SerializableConfig;
import me.Silverwolfg11.CommentConfig.node.ParentConfigNode;
import me.Silverwolfg11.CommentConfig.serialization.ClassDeserializer;
import me.Silverwolfg11.CommentConfig.serialization.ClassSerializer;
import me.Silverwolfg11.CommentConfig.serialization.NodeSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@SerializableConfig
@ConfigVersion(1.0)
public class MapConfig {

    @Comment("Worlds that should display town claims.")
    @Node("enabled-worlds")
    private List<String> enabledWorlds = Collections.singletonList("world");

    @Comment({"", "How often should the plugin render all towns? (In minutes)"})
    @Node("update-period")
    private int updatePeriod = 5;

    @Comment({"", "Layer Properties"})
    @Node("layer")
    private LayerInfo layerInfo = new LayerInfo();

    @Comment({"", "Fill Style.", "Properties about how claims should look on the map"})
    @Node("fill-style")
    private FillStyle fillStyle = new FillStyle();

    @Comment({"", "Icon Properties:", "Icons are placed at the center of a town's homeblock."})
    @Node("icon-info")
    private IconInfo iconInfo = new IconInfo();

    @SerializableConfig
    private class LayerInfo {
        @Comment("Name of the layer")
        private String name = "Towny";

        @Comment("Should the layer be toggleable by users?")
        @Node("show-controls")
        private boolean showControls = true;

        @Comment("Should the layer be invisible by default?")
        @Node("default-hidden")
        private boolean defaultHidden = false;

        @Comment({"Layer Priority.", "Don't need to touch this unless other Pl3xMap add-ons are interfering with the layer."})
        @Node("layer-priority")
        private int layerPriority = 5;

        @Comment({"The z-index on which the layer will display.", "Decrease if you want the layer to be more blended in with the map."})
        @Node("z-index")
        private int zIndex = 250;
    }

    @SerializableConfig
    private class FillStyle {
        @Comment("Whether to fill the claim with color")
        private boolean fill = true;

        @Node("fill-color")
        private String fillColor = "#3388ff";

        @Node("fill-opacity")
        private double fillOpacity = 0.2;

        @Comment("Use specified nation color as the fill color instead?")
        @Node("use-nation-color-fill")
        private boolean useNationColorFill = true;

        @Comment({"", "Whether to draw a stroke along the claim path."})
        private boolean stroke = true;

        @Comment("Stroke width in pixels")
        @Node("stroke-weight")
        private int strokeWeight = 3;

        @Node("stroke-color")
        private String strokeColor = "#3388ff";

        @Node("stroke-opacity")
        private double strokeOpacity = 1.0;

        @Comment("Use specified nation color as the stroke color instead?")
        @Node("use-nation-color-stroke")
        private boolean useNationColorStroke = false;

        @Node("use-town-color-fill")
        @Comment({"Use specified town color as the fill color instead?",
                  "This option will take priority over the 'use-nation-color-fill' option if it is enabled."})
        private boolean useTownColorFill = false;

        @Node("use-town-color-stroke")
        @Comment({"Use specified town color as the stroke color instead?",
                "This option will take priority over the 'use-nation-color-stroke' option if it is enabled."})
        private boolean useTownColorStroke = false;

        @Node("use-auto-colors")
        @Comment({"Automatically color each town so towns are visually distinct on the map.",
                  "Towns in the same nation will share the same color.",
                  "This only applies when the town and its nation have no map color configured."})
        private boolean useAutoColors = true;

        private transient Color awtFillColor, awtStrokeColor;
    }

    @SerializableConfig
    private class IconInfo {
        @Comment({"Icon for the town's homeblock.",
                "Put 'default' to use the bundled default icon.",
                "Put 'empty' to not place icons.",
                "Or use a valid image URL. If the URL fails to load, the bundled default icon is used instead."})
        @Node("town-icon")
        private String townIconImage = "default";

        @Comment({"Icon for a town if they are the capital of the nation.",
                "Put 'default' to use the bundled default icon.",
                "Put 'empty' to not place icons.",
                "Or use a valid image URL. If the URL fails to load, the bundled default icon is used instead."})
        @Node("capital-icon")
        private String capitalIconImage = "default";

        @Comment({"Icon for an outpost claim that will appear at the location of outpost spawns.",
                "Put 'default' to use the bundled default icon.",
                "Put 'empty' to not place icons at outposts.",
                "Or use a valid image URL. If the URL fails to load, the bundled default icon is used instead."})
        @Node("outpost-icon")
        private String outpostIconImage = "default";

        @Comment("Height of the icon")
        @Node("icon-height")
        private int iconSizeX = 35;

        @Comment("Width of the icon")
        @Node("icon-width")
        private int iconSizeY = 35;

        @Comment({"Size of the capital icon (both width and height).",
                "Keep it smaller than the regular town icon so it doesn't block the map when zoomed out."})
        @Node("capital-icon-size")
        private int capitalIconSize = 24;
    }

    public List<String> getEnabledWorlds() {
        return Collections.unmodifiableList(enabledWorlds);
    }

    public int getUpdatePeriod() {
        return updatePeriod;
    }

    @NotNull
    public LayerOptions getLayerOptions() {
        return new LayerOptions(
                layerInfo.name,
                layerInfo.showControls,
                layerInfo.defaultHidden,
                layerInfo.layerPriority,
                layerInfo.zIndex
        );
    }

    @NotNull
    public MarkerOptions.Builder buildMarkerOptions() throws NumberFormatException {
        if (fillStyle.awtFillColor == null)
            fillStyle.awtFillColor = Color.decode(fillStyle.fillColor);

        if (fillStyle.awtStrokeColor == null)
            fillStyle.awtStrokeColor = Color.decode(fillStyle.strokeColor);

        return MarkerOptions.builder()
                .fill(fillStyle.fill)
                .fillColor(fillStyle.awtFillColor)
                .fillOpacity(fillStyle.fillOpacity)
                .stroke(fillStyle.stroke)
                .strokeWeight(fillStyle.strokeWeight)
                .strokeColor(fillStyle.awtStrokeColor)
                .strokeOpacity(fillStyle.strokeOpacity);
    }

    public boolean useNationFillColor() {
        return fillStyle.useNationColorFill;
    }

    public boolean useNationStrokeColor() {
        return fillStyle.useNationColorStroke;
    }

    public boolean useTownFillColor() {
        return fillStyle.useTownColorFill;
    }

    public boolean useTownStrokeColor() {
        return fillStyle.useTownColorStroke;
    }

    public boolean useAutoColors() {
        return fillStyle.useAutoColors;
    }

    @Nullable
    public BufferedImage loadTownIcon(Logger errorLogger) {
        return loadIcon("town", iconInfo.townIconImage, errorLogger);
    }

    @Nullable
    public BufferedImage loadCapitalIcon(Logger errorLogger) {
        return loadIcon("capital", iconInfo.capitalIconImage, errorLogger);
    }

    @Nullable
    public BufferedImage loadOutpostIcon(Logger errorLogger) {
        return loadIcon("outpost", iconInfo.outpostIconImage, errorLogger);
    }

    // Load an icon by type ("town", "capital", "outpost").
    // Config values: "default" uses the bundled icon, "empty" disables the icon,
    // anything else is treated as an image URL that falls back to the bundled icon on failure.
    @Nullable
    private BufferedImage loadIcon(String type, String urlStr, Logger errorLogger) {
        if (urlStr.equalsIgnoreCase("empty"))
            return null;

        if (urlStr.equalsIgnoreCase("default")) {
            BufferedImage icon = loadBundledIcon(type);
            if (icon == null)
                errorLogger.severe("Could not load bundled default " + type + " icon!");
            return icon;
        }

        URL url;
        try {
            url = new URL(urlStr);
        } catch (MalformedURLException ex) {
            errorLogger.warning("Cannot load " + type + " icon due to an invalid URL '" + urlStr + "'. Using the bundled default icon instead!");
            return loadBundledIcon(type);
        }

        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            // Don't block the server thread for a long time on unreachable icon URLs
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            try (InputStream is = connection.getInputStream()) {
                return ImageIO.read(is);
            }
        } catch (IOException e) {
            errorLogger.warning("Error while loading " + type + " image icon from URL '" + urlStr + "'. Using the bundled default icon instead!");
            return loadBundledIcon(type);
        }
    }

    // Load a bundled default icon from the plugin jar resources.
    @Nullable
    private BufferedImage loadBundledIcon(String type) {
        String resourcePath = "/icons/" + type + ".png";
        try (InputStream is = getClass().getResourceAsStream(resourcePath)) {
            if (is == null)
                return null;

            return ImageIO.read(is);
        } catch (IOException e) {
            return null;
        }
    }

    public int getIconSizeX() {
        return iconInfo.iconSizeX;
    }

    public int getIconSizeY() {
        return iconInfo.iconSizeY;
    }

    public int getCapitalIconSize() {
        return iconInfo.capitalIconSize;
    }


    public static MapConfig loadConfig(File directory, Logger errorLogger) throws IOException {

        if (!directory.exists())
            directory.mkdir();

        File configFile = new File(directory, "config.yml");

        NodeSerializer serializer = new NodeSerializer();
        if (!configFile.exists()) {
            configFile.createNewFile();
            // Use save config mapping
            MapConfig config = new MapConfig();
            ParentConfigNode node = ClassSerializer.serializeClass(config);
            serializer.serializeToFile(configFile, node);
            return config;
        }
        else {
            ClassDeserializer deserializer = new ClassDeserializer();
            deserializer.setErrorLogger(errorLogger);
            return deserializer.deserializeClassAndUpdate(configFile, MapConfig.class, serializer);
        }

    }

}
