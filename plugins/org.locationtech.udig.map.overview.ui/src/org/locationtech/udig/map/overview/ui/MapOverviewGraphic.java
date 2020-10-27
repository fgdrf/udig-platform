package org.locationtech.udig.map.overview.ui;
/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2020, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.udig.mapgraphic.MapGraphic;
import org.locationtech.udig.mapgraphic.MapGraphicContext;
import org.locationtech.udig.project.render.IViewportModel;
import org.locationtech.udig.ui.graphics.ViewportGraphics;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

/**
 * 
 * a styled north arrow that actually points north!
 * <p>
 *
 * </p>
 * 
 * @author Frank Gasdorf
 * @since 2.3.0
 */
public final class MapOverviewGraphic implements MapGraphic {

    private Image worldImage;

    public MapOverviewGraphic() {
        ImageDescriptor imageDescriptor = AbstractUIPlugin.imageDescriptorFromPlugin(
                "org.locationtech.udig.map.overview.ui", "icons/worldoverview2.png");
        worldImage = imageDescriptor.createImage();
    }

    @Override
    public void draw(MapGraphicContext context) {
        // TODO calculate offset from border depending where to display over (top left or top right)
        Point vector = new Point(5, 5);
        Rectangle overviewMapDrawExtent = drawMapOverview(context, vector);
        drawCurrentExtent(context, vector, overviewMapDrawExtent);
    }

    private void drawCurrentExtent(MapGraphicContext context, Point vector,
            Rectangle overviewMapDrawExtent) {
        IViewportModel viewportModel = context.getViewportModel();
        ReferencedEnvelope viewportBounds = viewportModel.getBounds();

        CoordinateReferenceSystem mapCrs = viewportModel.getCRS();
        CoordinateReferenceSystem imageCrs = DefaultGeographicCRS.WGS84;
        ViewportGraphics graphics = context.getGraphics();
        try {

            MathTransform transformViewport2WGS84 = CRS.findMathTransform(mapCrs, imageCrs);
            Envelope viewportBoundsWGS84 = JTS.transform(viewportBounds, transformViewport2WGS84);

            Rectangle extentRectangle = calcExtentForOverviewImage(viewportBoundsWGS84);
            extentRectangle.translate(vector.x, vector.y);

            Color colorCurrentExtent = new Color(Color.RED.getRed(), Color.RED.getGreen(),
                    Color.RED.getBlue(), 80);
            graphics.setColor(colorCurrentExtent);

            graphics.fillRect(extentRectangle.x,
                    extentRectangle.y,
                    extentRectangle.width, extentRectangle.height);

            // current extent
            graphics.draw(extentRectangle);

            int x = extentRectangle.x + extentRectangle.width / 2;
            // line from left border to extent
            graphics.drawLine(x, overviewMapDrawExtent.y, x, extentRectangle.y);
            // line from right border to extent
            graphics.drawLine(x, extentRectangle.y + extentRectangle.height, x,
                    overviewMapDrawExtent.y + overviewMapDrawExtent.height);

            int y = extentRectangle.y + extentRectangle.height / 2;
            // line from left border to extent
            graphics.drawLine(overviewMapDrawExtent.x, y,
                    extentRectangle.x, y);
            // line from right border to extent
            graphics.drawLine(extentRectangle.x + extentRectangle.width, y,
                    overviewMapDrawExtent.x + overviewMapDrawExtent.width, y);

        } catch (FactoryException | TransformException e1) {
            e1.printStackTrace();
        }

    }

    private Rectangle calcExtentForOverviewImage(Envelope viewportBoundsWGS84) {
        // TODO calculate real extent in pixel
        double west = viewportBoundsWGS84.getMinX();
        double north = viewportBoundsWGS84.getMaxY();
        double east = viewportBoundsWGS84.getMaxX();
        double south = viewportBoundsWGS84.getMinY();

        if (west < -180) {
            west = -180;
        }
        if (west > 180) {
            west = 180;
        }
        if (north < -90) {
            north = -90;
        }
        if (north > 90) {
            north = 90;
        }
        if (east < -180) {
            east = -180;
        }
        if (east > 180) {
            east = 180;
        }
        if (south < -90) {
            south = -90;
        }
        if (south > 90) {
            south = 90;
        }
        west = 180.0 + west;
        north = 90.0 + north;
        east = 180.0 + east;
        south = 90.0 + south;
        double width = east - west;
        double height = north - south;
        if (width < 1) {
            width = 1;
        }
        if (height < 1) {
            height = 1;
        }
        org.eclipse.swt.graphics.Rectangle canvasBounds = worldImage.getBounds();
        int h = (int) ((float) canvasBounds.width * (float) canvasBounds.height
                / canvasBounds.width);

        int x = (int) Math.round(canvasBounds.width * west / 360.0);
        int y = (int) Math.round((h * north / 180.0));
        int fw = (int) Math.round(canvasBounds.width * width / 360.0);
        if (fw <= 1)
            fw = 2;
        int fh = (int) Math.round(h * height / 180.0);
        if (fh <= 1)
            fh = 2;
        int newy = h - y;
 
        return new Rectangle(x, newy, fw, fh);
    }

    private Rectangle drawMapOverview(MapGraphicContext context, Point vector) {
        org.eclipse.swt.graphics.Rectangle imageBounds = worldImage.getBounds();
        ViewportGraphics graphics = context.getGraphics();

        graphics.setColor(Color.WHITE); // TODO configurable color of the background
        graphics.fillRoundRect(vector.x, vector.y, imageBounds.width, imageBounds.height, 20, 20);


        graphics.drawImage(worldImage, vector.x, vector.y);
        graphics.setColor(Color.lightGray); // TODO configurable color of the border
        graphics.drawRoundRect(vector.x, vector.y, imageBounds.width, imageBounds.height, 20, 20);
        return new Rectangle(vector.x, vector.y, imageBounds.width, imageBounds.height);
    }
}
