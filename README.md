cat in hud

based on the wonderful [oneko.js](https://github.com/adryd325/oneko.js) <3

oneko.js is licensed MIT c adryd

## Customisation 
You can customise Neko by creating a resource pack:
- To use custom sprites place the sprite sheet in the pack at `assets/neko/textures/gui/oneko.png`. Make sure the png is the same size and the sprites are in the same place as in the [default](src/main/resources/assets/neko/textures/gui/oneko.png)
- To configure, create `assets/neko/neko_config.json`. See the [default](src/main/resources/assets/neko/neko_config.json) or example below:

```jsonc
{
  // not all fields need to be set, only those you wish to change
  // keep in mind that sprite is already affected by mc gui scale
  "scale": 0.5, // scale neko sprite
  "idle_interval": 30, // how often neko plays an idle animation (lower is more often)
  // these fields are scaled by the above `scale`
  "alert_distance": 32, // the distance at which neko exits idle and chases the cursor again
  "speed": 10 // speed
}
```