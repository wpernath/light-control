
    /**
     * 
     * @param {long} lightId 
     * @returns light or null
     */
    function findLightById(lightId) {
        for( var i=0; i < allLights.length; i++ ){
            if( allLights[i].id == lightId) {
                return allLights[i];
            }
        }
        return null;
    }

    /**
     * 
     * @param {long} roomId 
     * @returns 
     */
    function findRoomById(roomId) {
        for(var i=0; i < rooms.length; i++) {
            if( rooms[i].id == roomId) {
                return rooms[i];
            }
        }
        return null;
    }


    /**
     * 
     * @param {long} roomId 
     * @returns 
     */
    function roomHasLittenBulbs(roomId) {
        var room = findRoomById(roomId);
        if( room ) {
            for(var i=0; i < room.lights.length; i++) {
                var light = room.lights[i];
                if( light.on ) return true;
            }
        }
        return false;
    }

    /**
     * 
     * @param {long} roomId 
     */
    function toggleRoom(roomId) {
        let room = findRoomById(roomId);
        let state;
        if( room ) {
            room.on = !room.on;

            state = room.on;
            $.ajax({
                url: "/api/rooms/toggle",
                method: "GET",
                headers: {
                    "Content-Type": "text/plain"                    
                },
                data: {
                    "id": roomId,
                    "on": room.on
                }
            }).then(
                function(response){                

                    // update all light controls
                    for( var i=0; i < room.lights.length; i++ ){
                        let light = room.lights[i];
                        light.on  = room.on;
                        updateLightControls(light);
                    }                    

                    // update the room
                    updateRoomControls(room);

                },
                function(xhr){
                    console.log(xhr.status, xhr.statusText);
                }
            );
        }        
    }

    function changeColorTemperature(lightId, ct) {

    }

    function changeLightHue(lightId, hue) {

    }

    /**
     * 
     * @param {long} lightId 
     * @param {int} sat 
     */
    function changeLightSaturation(lightId, sat) {

    }

    /**
     * 
     * @param {long} lightId 
     * @param {int} bri 
     */
    function changeLightBrightness(lightId, bri) {
        if( bri ) {
            console.log("changeLightBrightness(" + lightId + ", " + bri + ")");
            let light = findLightById(lightId);
            if( light ){
                light.bri = bri;
                updateLightState(light);
            }
        }
    }

    /** 
     * Changes the light on/off state 
     * 
     */ 
    function toggleLightOnOff(lightId) {
        console.log("toggleLightOnOff(" + lightId + ")");
        let light = findLightById(lightId);
        if( light ) {
            light.on = !light.on;
            updateLightState(light);            
        }
    }

    /**
     * This function is calling the server to update the light on the hue bridges 
     */
    function updateLightState(light) {
        if( light ){
            console.log("updateLightState(" + light.id + ", " + light.on + ", " + light.bri + ")");
            $.ajax({
                url: "/api/lights/toggle",
                method: "GET",
                headers: {
                    "Content-Type": "text/plain"                    
                },
                data: {
                    "id": light.id,
                    "on": light.on,
                    "bri": light.bri
                }
            }).then(
                function(response){
                    updateLightControls(light);
                },
                function(xhr){
                    console.log(xhr.status, xhr.statusText);
                }
            );
        }        
    }


    /**
     * Makes sure, the controls which make up a room will reflect any 
     * changes
     * @param {*} room 
     */
    function updateRoomControls(room) {
        console.log("updateRoomControls(" + room.id + ")");
        let rounded = "#room_" + room.id;
        let img     = document.getElementById("img_room_" + room.id);
        let descr   = document.getElementById("room_descr_" + room.id);
        let imgName;

        $( rounded ).removeClass("bg-white bg-black fg-white fg-black");
        if(room.hasLittenBulbs()) {
            $( rounded ).addClass("bg-white fg-black");
            imgName = "https://img.icons8.com/color-glass/48/000000/light-on.png";   
            descr.textContent = room.numberOfLittenBulbs() + " lights on";
        }
        else {
            $( rounded ).addClass("fg-white bg-black");
            imgName = "https://img.icons8.com/color-glass/48/000000/light-off.png";   
            descr.textContent = "All lights off";
        }
        img.src = imgName;

    }

    /** 
     * This function is being called by the room event handler and on light click
     * It updates all the controls for a light.
     */
    function updateLightControls(light) {
        console.log("updateLightControls(" + light.id + ")");
        let lightId = light.id;
        let img = document.getElementById("img_light_"+lightId);
        let slid = document.getElementById("bri_" + lightId);
        let div = document.getElementById("state_" + lightId);
        if( img ) {
            var imgName;
            $( "#div_light_" + lightId ).removeClass("bg-white fg-black fg-white bg-black");
            if( light.on ) {
                var percent = (light.bri/254)*100;
                if( div ) div.textContent = percent.toFixed() + " %";
                imgName = "https://img.icons8.com/color-glass/48/000000/light-on.png";       
                $( "#div_light_" + lightId ).addClass("bg-white fg-black");                     
            }
            else {
                if( div ) div.textContent = "Off";
                imgName = "https://img.icons8.com/color-glass/48/000000/light-off.png";
                $( "#div_light_" + lightId ).addClass("fg-white bg-black");
            }
            img.src = imgName;

            if( slid ) {
                slid.disabled=!light.on;
                slid.value = light.bri;
            }
        }
    }


    /**
     * updates the details of a sensor (right now only the name)
     * @param {Long} id 
     */
    function updateSensorDetails(id) {
        console.log("updateSensor called with id = '" + id + "'");
        let sensor;
        for(let i = 0; i < sensors.length; i++ ){
            if( sensors[i].id == id) {
                sensor = sensors[i];
                console.log("found sensor: " + sensor);

                // update form elements
                document.getElementById("sensor_name").value = sensor.name;
                document.getElementById("sensor_type").value = sensor.type;
                document.getElementById("sensor_id").value = sensor.id;
                document.getElementById("sensor_product").value = sensor.productname;
                document.getElementById("sensor_fav").checked = sensor.isFavorite;
                break;
            }
        }
    }

    /**
     * 
     */
    function clearSensorForm() {
        console.log("clearSensorForm()");
        // update form elements
        document.getElementById("sensor_name").value = '';
        document.getElementById("sensor_type").value = '';
        document.getElementById("sensor_id").value = '';
        document.getElementById("sensor_product").value = '';
        document.getElementById("sensor_fav").checked = false;

    }

    /**
     * 
     */
    function saveSensorForm() {
        let sensor;
        let id = document.getElementById("sensor_id").value;
        if( !id ) {
            console.log("nothing selected to save");
        }
        for(let i = 0; i < sensors.length; i++ ){
            if( sensors[i].id == id) {
                sensor = sensors[i];
                console.log("found sensor: " + sensor);

                // update form elements
                sensor.name = document.getElementById("sensor_name").value;
                sensor.isFavorite = document.getElementById("sensor_fav").checked;

                $.ajaxSetup({
                    contentType: 'application/json',
                    processData: false
                });

                $.ajaxPrefilter(function(options, originalOptions, jqXHR){
                    if( options.data){
                        options.data=JSON.stringify(options.data);
                    }
                });

                $.ajax({
                    url: "/api/sensors/" + id,
                    method: "PUT",
                    data: {
                        newName: sensor.name,
                        isFavorite: sensor.isFavorite
                    }
                }).then(
                    function(response){
                        console.log("Sensor " + id + " updated on server" );
                        window.location.reload(true);
                        clearSensorForm();
                    },
                    function(xhr){
                        console.log(xhr.status, xhr.statusText);
                    }
                );

                break;
            }
        }
    }

