/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.symbology;

import gov.nasa.worldwind.WWObject;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.render.*;

/**
 * TacticalSymbol provides a common interface for displaying tactical point symbols from symbology sets. A tactical
 * symbol displays graphic and textual information about an object at a single geographic position at a particular point
 * in time. See the TacticalSymbol <a title="Tactical Symbol Usage Guide" href="http://goworldwind.org/developers-guide/symbology/tactical-symbols/"
 * target="_blank">Usage Guide</a> for instructions on using TacticalSymbol in an application.
 * <p/>
 * <h2>Construction</h2> Implementations of this interface provide support for symbols belonging to a specific symbology
 * set. For example, class {@link gov.nasa.worldwind.symbology.milstd2525.MilStd2525TacticalSymbol} provides support for
 * tactical symbols from the MIL-STD-2525 symbology specification.
 * <p/>
 * To create a tactical symbol, instantiate a concrete implementation appropriate for the desired symbology set. Pass a
 * string identifier, the desired geographic position, and (optionally) one or more symbol modifier key-value pairs to
 * the symbol's constructor. The tactical symbol creates a graphic appropriate for the string identifier and optional
 * symbol modifiers, and draws that graphic at the specified position when its render method is called. For example, a
 * symbol implementation may display a 3D object at the position, or display a screen space icon who's screen location
 * tracks the position. MIL-STD-2525 tactical symbols display a screen space icon with graphic and text modifiers
 * surrounding the icon.
 * <p/>
 * The format of the string identifier and the modifier key-value pairs are implementation dependent. For MIL-STD-2525,
 * the string identifier must be a 15-character alphanumeric symbol identification code (SIDC), and the modifier keys
 * must be one of the constants defined in MilStd2525TacticalSymbol's documentation.
 * <p/>
 * Since TacticalSymbol extends the Renderable interface, a tactical symbol is displayed either by adding it to a layer,
 * or by calling its render method from within a custom layer or renderable object. The simplest way to display a
 * tactical symbol is to add it to a {@link gov.nasa.worldwind.layers.RenderableLayer}. Here's an example of creating
 * and displaying a tactical symbol for a MIL-STD-2525 friendly ground unit using a RenderableLayer:
 * <p/>
 * <pre>
 * // Create a tactical symbol for a MIL-STD-2525 friendly ground unit. Since the SIDC specifies a ground symbol, the
 * // tactical symbol's altitude mode is automatically configured as WorldWind.CLAMP_TO_GROUND.
 * TacticalSymbol symbol = new MilStd2525TacticalSymbol("SFGPU----------", Position.fromDegrees(-120, 40, 0));
 *
 * // Create a renderable layer to display the tactical symbol. This example adds only a single symbol, but many
 * // symbols can be added to a single layer.
 * RenderableLayer symbolLayer = new RenderableLayer();
 * symbolLayer.addRenderable(symbol);
 *
 * // Add the layer to the world window's model and request that the window redraw itself. The world window draws the
 * // symbol on the globe at the specified position. Interactions between the symbol and the cursor are returned in the
 * // world window's picked object list, and reported to the world window's select listeners.
 * WorldWindow wwd = ... // A reference to your application's WorldWindow instance.
 * wwd.getModel().getLayers().add(symbolLayer);
 * wwd.redraw();
 * </pre>
 * <p/>
 * <h2>Position</h2> A symbol's geographic position defines where the symbol displays its graphic. Either the graphic's
 * geometric center is displayed at the position, or a specific location within the graphic (such as the bottom of a
 * leader line) is displayed at the position. This behavior depends on the symbol implementation, the string identifier,
 * and the symbol modifiers (if any).
 * <p/>
 * A symbol's altitude mode defines how the altitude component if the position is interpreted. Altitude mode may be
 * specified by calling {@link #setAltitudeMode(int)}. Recognized modes are: <ul> <li>WorldWind.CLAMP_TO_GROUND -- the
 * symbol graphic is placed on the terrain at the latitude and longitude of its position.</li>
 * <li>WorldWind.RELATIVE_TO_GROUND -- the symbol graphic is placed above the terrain at the latitude and longitude of
 * its position and the distance specified by its elevation.</li> <li>WorldWind.ABSOLUTE -- the symbol graphic is placed
 * at its specified position.</li> </ul>
 * <p/>
 * Tactical symbol implementations configure the altitude mode from the string identifier specified during construction.
 * For example, specifying the MIL-STD-2525 SIDC "SFGPU----------" specifies a friendly ground unit symbol, and causes a
 * tactical symbol to configure the altitude mode as WorldWind.CLAMP_TO_GROUND. The automatically configured mode can be
 * overridden by calling setAltitudeMode.
 * <p/>
 * <h2>Modifiers</h2> Symbols modifiers are optional attributes that augment or change a symbol's graphic. Modifiers can
 * be specified at construction by passing a list of key-value pairs, or after construction by calling {@link
 * #setModifier(String, Object)} with the modifier key and value. Which modifier keys are recognized by a tactical
 * symbol and how they affect the symbol's graphic is implementation dependent. Here's an example of setting the the
 * heading (direction of movement) modifier at construction for a MIL-STD-2525 friendly ground unit:
 * <p/>
 * <pre>
 * // Create a tactical symbol for a MIL-STD-2525 friendly ground unit, specifying the optional heading (direction of
 * // movement) modifier by passing in a list of key-value pairs.
 * AVList modifiers = new AVListImpl();
 * modifiers.setValue(AVKey.HEADING, Angle.fromDegrees(45));
 * TacticalSymbol symbol = new MilStd2525TacticalSymbol("SFGPU----------", Position.fromDegrees(-120, 40, 0),
 *     modifiers);
 * </pre>
 * <p/>
 * Here's an example of setting the same modifier after construction:
 * <p/>
 * <pre>
 * // Create a tactical symbol for a MIL-STD-2525 friendly ground unit.
 * TacticalSymbol symbol = new MilStd2525TacticalSymbol("SFGPU----------", Position.fromDegrees(-120, 40, 0));
 *
 * // Set the heading (direction of movement) modifier.
 * symbol.setModifier(AVKey.HEADING, Angle.fromDegrees(45));
 * </pre>
 * <p/>
 * Tactical symbol implementations apply modifiers from the string identifier specified during construction. For
 * example, given a MIL-STD-2525 symbol representing units, installation, or equipment, SIDC positions 11-12 specify the
 * echelon and task force modifiers (See MIL-STD-2525C, Appendix A). Here's an example of setting the echelon and task
 * force modifiers at construction for a MIL-STD-2525 friendly ground unit:
 * <p/>
 * <pre>
 * // Create a tactical symbol for a MIL-STD-2525 friendly ground unit. Specify the echelon modifier and task force
 * // modifiers by setting the SIDC characters 11-12 to "EA". This indicates that the ground unit is a team/crew task
 * // force (see MIL-STD-2525C, Appendix A, Table A-II).
 * TacticalSymbol symbol = new MilStd2525TacticalSymbol("SFGPU-----EA---", Position.fromDegrees(-120, 40, 0));
 * </pre>
 *
 * @author dcollins
 * @version $Id$
 */
public interface TacticalSymbol extends WWObject, Renderable, Highlightable
{
    /**
     * Indicates whether this symbol is drawn when in view.
     *
     * @return true if this symbol is drawn when in view, otherwise false.
     */
    boolean isVisible();

    /**
     * Specifies whether this symbol is drawn when in view.
     *
     * @param visible true if this symbol should be drawn when in view, otherwise false.
     */
    void setVisible(boolean visible);

    /**
     * Indicates a string identifier for this symbol. The format of the identifier depends on the symbol set to which
     * this symbol belongs. For symbols belonging to the MIL-STD-2525 symbol set, this returns a 15-character
     * alphanumeric symbol identification code (SIDC).
     *
     * @return an identifier for this symbol.
     */
    String getIdentifier();

    /**
     * Indicates this symbol's geographic position. See {@link #setPosition(gov.nasa.worldwind.geom.Position)} for a
     * description of how tactical symbols interpret their position.
     *
     * @return this symbol's current geographic position.
     */
    Position getPosition();

    /**
     * Specifies this symbol's geographic position. The specified position must be non-null, and defines where on the
     * globe this symbol displays its graphic. The position's altitude component is interpreted according to the
     * altitudeMode. The type of graphic this symbol displays at the position is implementation dependent.
     *
     * @param position this symbol's new position.
     *
     * @throws IllegalArgumentException if the position is <code>null</code>.
     */
    void setPosition(Position position);

    /**
     * Indicates this symbol's altitude mode. See {@link #setAltitudeMode(int)} for a description of the valid altitude
     * modes.
     *
     * @return this symbol's altitude mode.
     */
    int getAltitudeMode();

    /**
     * Specifies this symbol's altitude mode. Altitude mode defines how the altitude component of this symbol's position
     * is interpreted. Recognized modes are: <ul> <li>WorldWind.CLAMP_TO_GROUND -- this symbol's graphic is placed on
     * the terrain at the latitude and longitude of its position.</li> <li>WorldWind.RELATIVE_TO_GROUND -- this symbol's
     * graphic is placed above the terrain at the latitude and longitude of its position and the distance specified by
     * its elevation.</li> <li>WorldWind.ABSOLUTE -- this symbol's graphic is placed at its specified position.</li>
     * </ul>
     * <p/>
     * This symbol assumes the altitude mode WorldWind.ABSOLUTE if the specified mode is not recognized.
     *
     * @param altitudeMode this symbol's new altitude mode.
     */
    void setAltitudeMode(int altitudeMode);

    /**
     * Indicates whether this symbol draws its supplemental graphic modifiers.
     *
     * @return true if this symbol draws its graphic modifiers, otherwise false.
     */
    boolean isShowGraphicModifiers();

    /**
     * Specifies whether to draw this symbol's supplemental graphic modifiers.
     *
     * @param showGraphicModifiers true if this symbol should draw its graphic modifiers, otherwise false.
     */
    void setShowGraphicModifiers(boolean showGraphicModifiers);

    /**
     * Indicates whether this symbol draws its supplemental text modifiers.
     *
     * @return true if this symbol draws its text modifiers, otherwise false.
     */
    boolean isShowTextModifiers();

    /**
     * Specifies whether to draw this symbol's supplemental text modifiers.
     *
     * @param showTextModifiers true if this symbol should draw its text modifiers, otherwise false.
     */
    void setShowTextModifiers(boolean showTextModifiers);

    /**
     * Indicates the current value of a text or graphic modifier. See {@link #setModifier(String, Object)} for a
     * description of how modifiers values are interpreted.
     *
     * @param modifier the modifier key.
     *
     * @return the modifier value. May be <code>null</code>, indicating that this symbol does not display the specified
     *         modifier.
     *
     * @throws IllegalArgumentException if the modifier is <code>null</code>.
     */
    Object getModifier(String modifier);

    /**
     * Specifies the value of a text or graphic modifier. Which modifier keys are recognized how they affect the
     * symbol's graphic is implementation dependent. If the modifier has an implicit value and only needs to be enabled
     * (e.g. the MIL-STD-2525 location modifier), specify true as the modifier value. If the specified value is
     * <code>null</code>, the modifier is removed from this symbol.
     * <p/>
     * If the specified modifier represents a graphic or text modifier, its display is suppressed if
     * isShowGraphicModifiers or isShowTextModifiers, respectively, returns false.
     *
     * @param modifier the modifier key.
     * @param value    the modifier value. May be <code>null</code>, indicating that the modifier should be removed from
     *                 this symbol.
     *
     * @throws IllegalArgumentException if the modifier is <code>null</code>.
     */
    void setModifier(String modifier, Object value);

    /**
     * Returns this shape's normal (as opposed to highlight) attributes.
     *
     * @return this shape's normal attributes. May be <code>null</code>, indicating that the default highlight
     *         attributes are used.
     */
    TacticalSymbolAttributes getAttributes();

    /**
     * Specifies this symbol's normal (as opposed to highlight) attributes.
     *
     * @param normalAttrs the normal attributes. May be <code>null</code>, in which case default attributes are used.
     */
    void setAttributes(TacticalSymbolAttributes normalAttrs);

    /**
     * Returns this shape's highlight attributes.
     *
     * @return this shape's highlight attributes. May be <code>null</code>, indicating that the default attributes are
     *         used.
     */
    TacticalSymbolAttributes getHighlightAttributes();

    /**
     * Specifies this shape's highlight attributes.
     *
     * @param highlightAttrs the highlight attributes. May be <code>null</code>, in which case default highlight
     *                       attributes are used.
     */
    void setHighlightAttributes(TacticalSymbolAttributes highlightAttrs);
}
