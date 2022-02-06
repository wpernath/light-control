/*
    class Light {
        constructor(id, name, modelid, productid, productname, manufacturername, on, reachable, bri, hue, sat, colormode, xy) {
            this.id = id;
            this.name = name;
            this.modelid = modelid;
            this.productid = productid;
            this.productname = productname;
            this.manufacturername = manufacturername;
            this.on = on;
            this.reachable = reachable;
            this.bri = bri ;
            this.hue = hue;
            this.sat = sat;
            this.colormode = colormode;
            this.xy = xy;
        }

        hasColorSupport() {
            if( colormode != null && (colormode === 'ct' || colormode === 'xy' || colormode === 'hs')) return true;
            else return false; 
        }
    }


    class Room {

        constructor(id, name, on, bri) {
            this.id = id;
            this.name = name;
            this.on = on;
            this.bri = bri;
            this.lights = [];
        }

        addLight(light) {
            this.lights.push(light);
            return this;
        }

        findLight(id) {
            for( let i = 0; i < this.lights.length; i++) {
                if( this.lights[i].id === id ) {
                    return this.lights[i];
                }
            }
            return undefined;
        }

        hasLittenBulbs() {
            for(var i=0; i < this.lights.length; i++) {
                var light = this.lights[i];
                if( light.on ) return true;
            }
            return false;
        }

        numberOfLittenBulbs() {
            let num = 0;
            for(var i=0; i < this.lights.length; i++) {
                var light = this.lights[i];
                if( light.on ) num++;
            }            
            return num;
        }
        
    }  


    const rooms = [
        {#for room in rooms.orEmpty}
        new Room({room.id}, '{room.name}', {room.action ? room.action.on : 'false'}, {room.action ? room.action.bri : 0})
            {#for light in room.allLights.orEmpty}
            .addLight(new Light(
                {light.id},
                '{light.name}',
                '{light.modelid}',
                '{light.productid}',
                '{light.productname}',
                '{light.manufacturername}',
                {#if light.state != null }{#if light.state.on != null }{light.state.on}{#else}false{/if},
                {#if light.state.reachable != null }{light.state.reachable}{#else}true{/if},
                {#if light.state.bri != null }{light.state.bri}{#else}undefined{/if},
                {#if light.state.hue != null }{light.state.hue}{#else}undefined{/if},
                {#if light.state.sat != null }{light.state.sat}{#else}undefined{/if},
                '{light.state.colormode}',
                {#if light.state.xy != null }[
                    {light.state.xy[0]},
                    {light.state.xy[1]}
                ]{#else}undefined{/if}
                {/if}   
            ))
            {/for},
        {/for}
    ];    
*/


