<h1 align="center">McUtils</h1>
<p align="center">Just a collection of utils for minecraft development </p>
<br/><br/>

## Contents:

- Color Utils
- VaultHook
- Item Utils
    - ItemBuilder
    - SkullCreator
    - InventoryUtils
    - YamlItem
    - ItemStack Serializer to base64
- Progress Utils
- Money Formatting Utils
- Json Wrapper for easier handling of json data
- Lang Config Utils
- Chat Utils
    - Chat Input Handler
    - Message Utils
- Gui
    - Static Gui Base
    - Config System (More info [here](#gui-config-system))

## JavaDocs

You can find the JavaDocs for this library [here](https://tohjuler.github.io/McUtils/).

## Maven

You can use this library in your project by adding the following repository and dependency to your `pom.xml`:
<br/>
<br/>
Repository:

```xml

<repository>
    <id>tohjuler-repository-releases</id>
    <name>Tohjuler's Repository</name>
    <url>https://repo.tohjuler.dk/releases</url>
</repository>
```

Dependency:

```xml

<dependency>
    <groupId>dk.tohjuler</groupId>
    <artifactId>McUtils</artifactId>
    <version>{version}</version>
</dependency>
```

Remember to replace `{version}` with the version you want to use.

## Gui Config System

The gui config system aims to make it possible to edit the gui from a yml file,
while still upholding the dev experience of creating the gui in code.

Example:

ExampleGui.java

```java
public class ExampleGui extends ConfigBasedGui {
    public ExampleGui() {
        super(
                "example-gui", // Id of the gui
                "&8Just a example gui", // Title of the gui
                6, // Rows of the gui
                FillType.TOP_BOTTOM, // Fill type of the gui : TOP, BOTTOM, TOP_BOTTOM, SIDES, AROUND, ALL, NONE
                new ItemBuilder(XMaterial.BLUE_STAINED_GLASS_PANE) // Item to fill the gui with
                        .setDisplayName(" ")
        );
    }

    @Override
    public void init() {
        item( // Create an item
                "info", // Id of the item, used as an identity in the config
                4, // The slot
                new ItemBuilder(XMaterial.BOOK) // The item
                        .setDisplayName("&6&lInformation")
        ).add(); // Add the item

        // Example on dynamic item
        item(
                "player-info",
                22,
                new ItemBuilder(XMaterial.PLAYER_HEAD)
                        .setDisplayName("&b%player_name%")
                        .addLore(
                                "&7PlaceholderAPI is automatically, used if it is installed on the server",
                                "%placeholder%",
                                "Try to click me!"
                        )
        ).replacer( // A replacer is used to replace placeholders in the item
                new Replacer() {
                    @Override
                    public void replace(Player p) {
                        replace(
                                "%placeholder%", // Regex to match
                                matchedString -> "This is a placeholder" // Function to replace the matched string
                        );
                    }
                }
        ).clickAction((player, clickEvent) -> {
            player.sendMessage("You clicked the player info item");
            clickEvent.setCancelled(false); // The event is cancelled by default
        }).add();

        // Example on an Item that has a condition to be shown
        item(
                "condition-item",
                40,
                new ItemBuilder(XMaterial.DIAMOND)
                        .setDisplayName("&aDiamond")
        ).show(player -> // Condition to show the item
                player.hasPermission("example.permission") // This item will only be shown if the player has the permission "example.permission"
        ).add();
    }
}
```

You need to register the gui in your main class:

```java
GuiManager guiManager = new GuiManager(
        this, // The javaPlugin instance
        new ExampleGui() // The guis to register
        // More guis can be added here
);
        
guiManager.open(player,"example-gui"); // Open the gui for the player, from a gui id
guiManager.open(player,ExampleGui.class); // Open the gui for the player, from a gui class

guiManager.reload(); // Reload all the guis
```
