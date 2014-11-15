package wide.modules.gui.core.view.components;

import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

class MapNode extends Label
{
	/*public MapNode(WorldObjectEntity entity)
	{
		this.entity = entity;
	}

	private WorldObjectEntity entity;*/
}

public class WorldMap extends StackPane
{
    /*
	private final FloatProperty scale = new SimpleFloatProperty(1.f);

	private final ObjectProperty<ViewPort> viewPort = new SimpleObjectProperty<ViewPort>();

	class MapImplementation extends Pane
	{
		@Override
		protected void layoutChildren()
		{
			super.layoutChildren();
		}

		private void refresh()
		{
			*
			 * // TODO Remove this ConnectionSettings op = new
			 * ConnectionSettings("server", "server"); DatabaseConnection con =
			 * new DatabaseConnection("world_434"); con.open(op);
			 *
			 * map.getChildren().clear();
			 *
			 * final List<DynamicMapping> list =
			 * DynamicMapping.CreateArrayFromResult(con.query("SELECT *")); for
			 * (DynamicMapping mapping : list) map.getChildren().add(new
			 * CreatureMapNode(list));
			 *
			 * con.close();
			 *
		}
	}

	private final MapImplementation map = new MapImplementation();

	class Controls extends BorderPane
	{
		public Controls()
		{
			ScrollBar scrollBar = new ScrollBar();
			AnchorPane.setRightAnchor(scrollBar, 20.d);
			AnchorPane.setTopAnchor(scrollBar, 20.d);

			scrollBar.setOrientation(Orientation.VERTICAL);

			scrollBar.setMin(0.d);
			scrollBar.setMax(20.d);

			setRight(scrollBar);
		}
	}

	private final Controls controls = new Controls();

	public WorldMap()
	{
		viewPort.addListener(new ChangeListener<ViewPort>()
		{
			@Override
			public void changed(ObservableValue<? extends ViewPort> observable,
					ViewPort oldValue, ViewPort newValue)
			{
				if (!oldValue.equals(newValue))
					return;

				map.refresh();
			}
		});

		getChildren().addAll(map, controls);
	}

	public ObjectProperty<ViewPort> viewportProperty()
	{
		return viewPort;
	}

	public ViewPort getViewport()
	{
		return viewPort.get();
	}

	public void setViewport(ViewPort viewport)
	{
		this.viewPort.set(viewport);
	}
*/
}
