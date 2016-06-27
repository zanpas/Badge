angular.module('badge.controllers', ['ui.calendar','ngGrid'])//, 'ui.bootstrap'

.controller('AppCtrl',function($scope, $rootScope, uiCalendarConfig) {
  $rootScope.users = [{id: '0', firstname:'super', lastname:'user', username:'super_user', password:'super', profile:'admin'},
                      {id: '1', code:1, firstname:'serena', lastname:'ardissone', username:'sardissone', password:'serena', profile:'user', qualify:'IMP'},
                      {id: '2', code:2, firstname:'pasquale', lastname:'zannino', username:'pzannino', password:'pasquale', profile:'user', qualify:'IMP'},
                      {id: '3', firstname:'domenico', lastname:'frosina', username:'dfrosina', password:'domenico', profile:'admin', qualify:'DIR'},
                      {id: '4', firstname:'carlo', lastname:'siciliano', username:'csiciliano', password:'carlo', profile:'manager', qualify:'DIR'}
  ];

  $rootScope.titleHeader = 'Login';
  $scope.loginData = {};

  $scope.logout = function() {
    alert('vecchio logout:  ' + $rootScope.titleHeader); //alert
    $rootScope.titleHeader = 'Login';
    $scope.loginData = {};
    $rootScope.userLogged = undefined;
    alert('nuovo logout:  ' + $rootScope.titleHeader);  //alert
  };

  // Perform the login action when the user submits the login form
  $scope.login = function() {
    alert('vecchio login:  ' + $rootScope.titleHeader);  //alert
    console.log('Doing login', $scope.loginData);

    for (var i = 0; i < $rootScope.users.length; i++) {
      if($rootScope.users[i].username==$scope.loginData.username && $rootScope.users[i].password==$scope.loginData.password){
        $rootScope.userLogged = $rootScope.users[i];
      }
    };

    if ($rootScope.userLogged) {
      $rootScope.titleHeader = 'Dashboard';
      console.log('Utente ' + $rootScope.userLogged.username + ' loggato con successo');
      $rootScope.eventFilter.user = $rootScope.userLogged.id;
      if($rootScope.userLogged.profile=='admin'){
        $rootScope.eventCategories = [{id:1, name:'Donatori Sangue'}, {id:2, name:'Permesso'}, {id:3, name:'Malattia'}, {id:4, name:'Allattamento'}, {id:5, name:'Ferie'}, {id:6, name:'Lavoro Straordinario'}, {id:7, name:'Festa'}, {id:8, name:'Chiusura'}];
      }
      //$rootScope.searchEvents('myCalendar1');
      $rootScope.searchEventsByFilters();
      //uiCalendarConfig.calendars['myCalendar1'].fullCalendar( 'addEventSource', $rootScope.userEvents );
      //uiCalendarConfig.calendars['myCalendar1'].fullCalendar('refetchEvents');
      //uiCalendarConfig.calendars['myCalendar1'].fullCalendar('render');
    }else{
      console.log('Nessun utente trovato per i parametri inseriti');
      alert('Utente non trovato');
    }
      alert('nuovo login:  ' + $rootScope.titleHeader); //alert
  };

})

.controller('CalendarCtrl', function($scope, $rootScope, $stateParams, $ionicModal, $compile, $filter, $window, uiCalendarConfig, popupService, CalendarEvent) {

      $scope.showEventTitle = false;

      $scope.showCalendar = true;
      $scope.showEventCalendar = function(){
        $scope.showCalendar = !$scope.showCalendar;
      }

      $rootScope.eventCategories = [{id:1, name:'Donatori Sangue'}, {id:2, name:'Permesso'}, {id:3, name:'Malattia'}, {id:4, name:'Allattamento'}, {id:5, name:'Ferie'}, {id:6, name:'Lavoro Straordinario'}];

      /* eventi source that pulls from google.com */
      $scope.eventSource = {
        /*
         url: "http://www.google.com/calendar/feeds/usa__en%40holiday.calendar.google.com/public/basic",
         className: 'gcal-event',           // an option!
         currentTimezone: 'America/Chicago' // an option!
         */
      };

      $rootScope.userEvents = [
        //{id:1, title: 'AVIS - Donazione sangue',start: new Date(2015, 6, 1), end: new Date(2015, 6, 2), allDay: true, overlap: false, rendering: 'background', color: '#ff9f89', user:1},
        //{id:2, title: 'Ferie',start: new Date(y, m, 6),end: new Date(y, m, 8), rendering: 'background', color: 'green', user:1},
        //{id:3, title: 'Permesso',start: new Date(y, m, 15, 16, 0),end: new Date(y, m, 15, 18, 0), allDay: false, color: 'orange', user:1}
      ];
      $rootScope.holidays = [];
      $rootScope.eventSources = [];//$rootScope.userEvents $rootScope.holidays;
      $rootScope.eventFilter = {};

      $scope.gridOptions = {
        data: 'userEvents',
        //showFilter : true,
        enableColumnResize: true,
        columnDefs: [{field:'id', displayName:'Id', visible:false},{field:'title', displayName:'Titolo', width:'30%', resizable:true}, {field:'start', displayName:'Dal', cellFilter:'date:\'dd/MM/yyyy HH:mm\'', width:'30%', resizable:true}, {field:'end', displayName:'Al', cellFilter:'date:\'dd/MM/yyyy HH:mm\'', width:'30%', resizable:true},{displayName:'Operazioni',cellTemplate:'<div ng-show="userLogged && userLogged.profile==\'admin\'"><a ng-click="$event.stopPropagation(); delEvent(\'myCalendar1\', row);" >Elimina</a></div>', width:'10%', resizable:true, sortable:false}]
      };
      /*
       function autoColWidth(colDefs, row) {
       var totalChars = 0;
       for ( var colName in row) {
       // Convert numbers to strings here so length will work.
       totalChars += (new String(row[colName])).length;
       }
       colDefs.forEach(function(colDef) {
       var numChars = (new String(row[colDef.field])).length;
       colDef.width = (numChars / totalChars * 100) + "%";
       });
       return colDefs;
       }
       */

      $rootScope.searchHolidays = function(){
        //uiCalendarConfig.calendars['myCalendar1'].fullCalendar( 'removeEventSource', $rootScope.holidays);

        if($rootScope.holidays && $rootScope.holidays.length>0){
          $rootScope.holidays.splice(0, $rootScope.holidays.length);
        }

        CalendarEvent.query({categoryList:[7,8]},
            function ( CalendarEventResult ) {
              if(CalendarEventResult.numResults>0){
                $rootScope.holidays = CalendarEventResult.events;
                for (var i = 0; i < $rootScope.holidays.length; i++) {
                  if($rootScope.holidays[i].category == 7){
                    $rootScope.holidays[i].textColor='white';
                    $rootScope.holidays[i].color='red';
                  }else if($rootScope.holidays[i].category == 8){
                    $rootScope.holidays[i].textColor='white';
                    $rootScope.holidays[i].color='red';
                  }
                  $rootScope.holidays[i].start=new Date($rootScope.holidays[i].start);
                  $rootScope.holidays[i].end=new Date($rootScope.holidays[i].end);
                };
                //uiCalendarConfig.calendars['myCalendar1'].fullCalendar( 'addEventSource', $rootScope.holidays );
                //uiCalendarConfig.calendars['myCalendar1'].fullCalendar('refetchEvents');
              }
            },
            function(error) {
              $scope.alertMessage = ('Errore durante la ricerca degli eventi');
              console.log('error: ' + JSON.stringify(error));
            }
        );
      }


      $rootScope.searchEventsByFilters = function(){
        console.log('searchEventsByFilters INIT');
        uiCalendarConfig.calendars['myCalendar1'].fullCalendar( 'removeEventSource', $rootScope.userEvents);
        //$rootScope.userEvents = [];
        var params = {};
        if($rootScope.eventFilter.user){

          if($rootScope.userEvents && $rootScope.userEvents.length>0){
            $rootScope.userEvents.splice(0, $rootScope.userEvents.length);
          }

          $rootScope.searchHolidays();

          params.userId = $rootScope.eventFilter.user;
          CalendarEvent.query(params,
              function ( CalendarEventResult ) {
                console.log('EVENTI RICEVUTI: ' + JSON.stringify(CalendarEventResult));
                if(CalendarEventResult.numResults>0){
                  for (var i = 0; i < CalendarEventResult.events.length; i++) {
                    var currentCalendarEvent = CalendarEventResult.events[i];

                    var currentUserEvent = {id:currentCalendarEvent.id, title: currentCalendarEvent.title, user:currentCalendarEvent.userid, rendering: 'background', category:currentCalendarEvent.category, start:currentCalendarEvent.start,end:currentCalendarEvent.end};

                    currentUserEvent.start = new Date(currentCalendarEvent.start);
                    currentUserEvent.end = new Date(currentCalendarEvent.end);

                    if(currentCalendarEvent.category == 1){
                      //don sangue
                      currentUserEvent.color = '#ff9f89';
                    }else if(currentCalendarEvent.category == 2){
                      //Permesso
                      currentUserEvent.color = 'orange';
                    }else if(currentCalendarEvent.category == 3){
                      //malattia
                      currentUserEvent.color = 'green';
                    }else if(currentCalendarEvent.category == 4){
                      //allattamento
                      currentUserEvent.color = 'pink';
                    }else if(currentCalendarEvent.category == 5){
                      //ferie
                      currentUserEvent.color = 'brown';
                    }else if(currentCalendarEvent.category == 6){
                      // straordinario
                      currentUserEvent.color = 'red';
                    }else if(currentCalendarEvent.category == 7){
                      // festa
                      //currentUserEvent.textColor='white';
                      currentUserEvent.color='red';
                      //$rootScope.holidays.push(currentUserEvent);
                    }else if(currentCalendarEvent.category == 8){
                      // chiusura
                      //currentUserEvent.textColor='white';
                      currentUserEvent.color='red';
                      //$rootScope.holidays.push(currentUserEvent);
                    }

                    console.log('EVENTO FINALE: ' + JSON.stringify(currentUserEvent));
                    $rootScope.userEvents.push(currentUserEvent);

                  };
                }

                uiCalendarConfig.calendars['myCalendar1'].fullCalendar( 'addEventSource', $rootScope.userEvents );
                //uiCalendarConfig.calendars['myCalendar1'].fullCalendar( 'addEventSource', $rootScope.holidays );
                //uiCalendarConfig.calendars['myCalendar1'].fullCalendar('refetchEvents');
                //$scope.renderCalender('myCalendar1');

                $scope.alertMessage = (CalendarEventResult.message);
              },
              function(error) {
                $scope.alertMessage = ('Errore durante la ricerca degli eventi');
                console.log('error: ' + JSON.stringify(error));
              }
          )
        }

        console.log('searchEventsByFilters END');
        return $rootScope.userEvents;
      }
      /*
       $rootScope.searchEvents = function(calendar){
       console.log('searchEvents INIT');
       if($rootScope.eventFilter.user){
       console.log('Ricerca eventi per utente ' + $rootScope.eventFilter.user);
       if($rootScope.userEvents && $rootScope.userEvents.length>0){
       $rootScope.userEvents.splice(0, $rootScope.userEvents.length);
       }
       //uiCalendarConfig.calendars[calendar].fullCalendar( 'removeEventSource', $rootScope.userEvents);
       //uiCalendarConfig.calendars['myCalendar1'].fullCalendar( 'removeEvents', {});
       $rootScope.searchEventsByFilters();
       //uiCalendarConfig.calendars[calendar].fullCalendar( 'addEventSource', $rootScope.userEvents );
       //uiCalendarConfig.calendars[calendar].fullCalendar('refetchEvents');
       }
       console.log('searchEvents END');
       return $rootScope.userEvents;
       }
       */
      /* alert on eventClick */
      $scope.alertOnEventClick = function( date, jsEvent, view){
        $scope.alertMessage = (date.title + ' was clicked ');
      };
      /* alert on Drop */
      $scope.alertOnDrop = function(event, delta, revertFunc, jsEvent, ui, view){
        $scope.alertMessage = ('Event Droped to make dayDelta ' + delta);
      };
      /* alert on Resize */
      $scope.alertOnResize = function(event, delta, revertFunc, jsEvent, ui, view ){
        $scope.alertMessage = ('Event Resized to make dayDelta ' + delta);
      };

      $scope.cleanSelectionData = function() {
        $scope.startDate = undefined;
        $scope.endDate = undefined;
        //$scope.dateSelected = undefined;
        $scope.allDay = undefined;
        $scope.periodSelected = undefined;

        $scope.newEvent = {};
        $scope.errorCalendarModal = undefined;
        $scope.showEventTitle = false;

        $scope.startDate4 = undefined;
        $scope.startTime4 = undefined;
        $scope.endDate4 = undefined;
        $scope.endTime4 = undefined;
      }

      $scope.showCalendarMessage = function(message) {
        console.log(message);
        $scope.alertMessage = (message);
        //alert(message);
        document.getElementById("AlertArea").focus();
      }
      /* imposta i dati per un evento da inserire o modificare */
      $scope.buildNewEvent = function() {
        console.log('buildNewEvent INIT');

        $scope.newEvent.user = $rootScope.userLogged.id;

        var mystartdate4Obj = document.getElementById("startDate4");
        console.log('mystartdate4Obj: ' + mystartdate4Obj);
        var mystartdate4 = document.getElementById("startDate4").value;
        console.log('mystartdate4: ' + mystartdate4);
        //var mystartdate4Array = mystartdate4.split("-");
        //$scope.newEvent.start = new Date(mystartdate4Array[0],mystartdate4Array[1],mystartdate4Array[2]);

        var mystartTime4Obj = document.getElementById("startTime4");
        console.log('mystartTime4Obj: ' + mystartTime4Obj);
        var mystartTime4 = document.getElementById("startTime4").value;
        console.log('mystartTime4: ' + mystartTime4);

        //var mystartTime4Array = undefined;
        //if(mystartTime4){
        //  mystartTime4Array = mystartTime4.split(":");
        //  $scope.newEvent.start = new Date(mystartdate4Array[0],mystartdate4Array[1],mystartdate4Array[2], mystartTime4Array[0], mystartTime4Array[1]);
        //}

        var myendDate4 = document.getElementById("endDate4").value;
        console.log('myendDate4: ' + myendDate4);
        var myendTime4 = document.getElementById("endTime4").value;
        console.log('myendTime4: ' + myendTime4);

        //$scope.newEvent.start = $scope.startDate;
        var myStartDateStr = mystartdate4+' 00:00:00.000';
        if(mystartTime4){
          myStartDateStr = mystartdate4+' '+mystartTime4;
        }
        console.log('myStartDateStr: ' + myStartDateStr);
        var myStartDate = new Date(myStartDateStr);

        console.log('myStartDate: ' + myStartDate);
        $scope.newEvent.start = myStartDate.getTime();
        console.log('myStartDateTIME: ' + $scope.newEvent.start);
        //$scope.newEvent.end = $scope.endDate;
        var myEndDateStr = myendDate4+' 00:00:00.000';
        if(myendTime4){
          myEndDateStr = myendDate4+' '+myendTime4;
        }
        console.log('myEndDateStr: ' + myEndDateStr);
        var myEndDate = new Date(myEndDateStr);
        console.log('myEndDate: ' + myEndDate);
        $scope.newEvent.end = myEndDate.getTime();
        console.log('myEndDateTIME: ' + $scope.newEvent.end);

        if(!$scope.showEventTitle){
          for (var i = 0; i < $scope.eventCategories.length; i++) {
            if($scope.eventCategories[i].id==$scope.newEvent.category){
              $scope.newEvent.title=$scope.eventCategories[i].name;
              break;
            }
          };
        }

        console.log('EventObj: ' + JSON.stringify($scope.newEvent, null, 2));

        console.log('buildNewEvent END');
      }
      /* add custom event*/
      $scope.updateEvent = function(calendar) {
        console.log('updateEvent INIT');
        $scope.buildNewEvent();
        if($scope.showEventTitle && !$scope.newEvent.title){
          $scope.errorCalendarModal = 'Titolo evento obbligatorio!';
        }else if(!$scope.newEvent.start){
          $scope.errorCalendarModal = 'Data inizio evento obbligatoria!';
        }else if(!$scope.newEvent.end){
          $scope.errorCalendarModal = 'Data fine evento obbligatoria!';
        }else if(!$scope.newEvent.category){
          $scope.errorCalendarModal = 'Categoria evento obbligatoria!';
        }else if($scope.newEvent.end < $scope.newEvent.start){
          $scope.errorCalendarModal = 'La data fine evento precede quella di inizio!';
        }else{

          if($rootScope.holidays){
            for (var i = 0; i < $rootScope.holidays.length; i++) {
              if($scope.newEvent.category != 6 && $scope.newEvent.start < $rootScope.holidays[i].end && $rootScope.holidays[i].start < $scope.newEvent.end){
                $scope.errorCalendarModal = 'Periodo indicato non valido per questa categoria, include l\'evento ' + $rootScope.holidays[i].title;
                return;
              }
            };
          }

          if ($scope.newEvent.category != 6 || $scope.newEvent.category != 7 || $scope.newEvent.category != 8) {
            var startEvent = new Date($scope.newEvent.start);
            var endEvent = new Date($scope.newEvent.end);
            while(startEvent < endEvent){
              var day = startEvent.getDay();
              if(day == 0 || day == 6) {
                $scope.errorCalendarModal = 'Periodo indicato non valido per questa categoria, non includere le giornate di sabato o domenica';
                return;
              }
              startEvent.setDate(startEvent.getDate() + 1);
            }
          };

          // verifica se l'evento include un evento diverso festa o chiusura
          CalendarEvent.query({categoryList:[1,2,3,4,5,6], override:true, start:new Date($scope.newEvent.start), end:new Date($scope.newEvent.end)},
              function ( CalendarEventResult ) {
                if(CalendarEventResult.numResults>0){
                  //return  CalendarEventResult.events;
                  $scope.errorCalendarModal = 'Il nuovo evento si sovrappone all\'evento ' + CalendarEventResult.events[0].title;
                  return;
                }
              },
              function(error) {
                $scope.alertMessage = ('Errore durante la ricerca degli eventi');
                console.log('error: ' + JSON.stringify(error));
              }
          );

          $scope.addEvent(calendar);
        }
        console.log('updateEvent END');
      }
      /* add custom event*/
      $scope.addEvent = function(calendar) {
        console.log('addEvent INIT');

        var newCalendarEvent = new CalendarEvent();
        //newCalendarEvent.id=33;
        newCalendarEvent.start=$scope.newEvent.start;
        newCalendarEvent.end=$scope.newEvent.end;
        newCalendarEvent.title=$scope.newEvent.title;
        newCalendarEvent.category=$scope.newEvent.category;
        newCalendarEvent.userid=$scope.newEvent.user;
        newCalendarEvent.description=$scope.newEvent.note;
        newCalendarEvent.$save(
            function(response){
              $scope.showCalendarMessage('Creato nuovo evento ' + newCalendarEvent.title);
              $scope.cleanSelectionData();
              $rootScope.searchEventsByFilters();
              //$scope.newEvent.color='red';
              //$rootScope.userEvents.push(newCalendarEvent);
              console.log('response: ' + JSON.stringify(response));
            },
            function(responseError){
              $scope.showCalendarMessage('Errore durante la creazione dell\'evento ' + newCalendarEvent.title);
              //console.log('responseError: ' + JSON.stringify(responseError));
            }
        );

        //$scope.cleanSelectionData();
        $scope.closeEventModal();
        //$scope.renderCalender(calendar);

        console.log('addEvent END');
      };

      $scope.deleteEventInPopup = '<div><button id="delEventBtn" type="button" class="btn btn-primary" ng-click="delEvent(\'myCalendar1\',row)">Elimina</button></div>';

      $scope.delEvent = function(calendar,row){
        console.log("Cancellazione evento " + row.entity.title)
        $scope.deleteEventById(calendar, row.entity.id, row.entity.title);
      }

      /* remove event */
      $scope.remove = function(calendar,index) {
        var eventToRemove = $rootScope.userEvents[index];
        $scope.deleteEventById(calendar, eventToRemove.id, eventToRemove.title);
      };

      /* remove event */
      $scope.deleteEventById = function(calendar,id, title) {
        var calendarEventToDelete = new CalendarEvent();
        calendarEventToDelete.id = id;

        console.log("Cancellazione evento " + title + ' ... ');
        if(popupService.showPopup('Sei sicuro di voler eliminare l\'evento?')){
          calendarEventToDelete.$delete({id: calendarEventToDelete.id },
              //function(){
              //  $scope.showCalendarMessage('Evento ' + eventToRemove.title + ' cancellato.');
              //  $scope.searchEvents(calendar);
              //  //$window.location.href='';
              //},
              // success callback
              function (returnValue, responseHeaders) {
                $scope.showCalendarMessage('Evento ' + title + ' cancellato.');
                $scope.searchEventsByFilters();
                console.log('returnValue: ' + JSON.stringify(returnValue));
                console.log('responseHeaders: ' + JSON.stringify(responseHeaders));
              },
              // error callback
              function (httpResponse) {
                $scope.showCalendarMessage('Errore durante la cancellato dell\'evento ' + title + '.');
                console.log('httpResponse: ' + JSON.stringify(httpResponse));
              }
          );
        }

        //$scope.cleanSelectionData();
        //$scope.renderCalender(calendar);
      };
      /* Change View */
      $scope.changeView = function(view,calendar) {
        uiCalendarConfig.calendars[calendar].fullCalendar('changeView',view);
      };
      /* Change View */
      $scope.renderCalender = function(calendar) {
        if(uiCalendarConfig.calendars[calendar]){
          uiCalendarConfig.calendars[calendar].fullCalendar('render');
        }
      };
      /* Render Tooltip */
      $scope.eventRender = function( event, element, view ) {
        //element.attr('title', event.title);

        //element.attr({'tooltip': event.title, 'tooltip-append-to-body': true});
        //  element.content = {'text': event.title,'title': event.title};
        //  $compile(element)($scope);
      };
      /* config object */
      $scope.uiConfig = {
        calendar:{
          lang: 'it',
          minTime:'07:00:00',
          header:{
            left: 'today prev,next',
            center: 'title',
            right: 'month,agendaWeek,agendaDay'
          },
          height: 450,
          editable: false,
          droppable:false,
          selectable: true,
          select: function(start, end, jsEvent, view) {
            console.log('select INIT');

            $scope.cleanSelectionData();

            /*
             if(start.hasTime() || end.hasTime()){
             $scope.newEvent.allDay = false;
             }else{
             $scope.newEvent.allDay = true;
             }
             */

            $scope.startDate = new Date(start);
            console.log('start: ' + start);
            console.log('start formatted: ' + start.format("dddd, MMMM Do YYYY, h:mm:ss a"));
            console.log('start MOMENT ISO: ' + start.format('YYYY-MM-DD')+'T'+start.format('HH:mm:ss.SSS'));
            console.log('start dec UTC: ' + $scope.startDate.getUTCFullYear() + '-' + ($scope.startDate.getUTCMonth() + 1) + '-' + $scope.startDate.getUTCDate() + 'T' + $scope.startDate.getUTCHours() + ':' + $scope.startDate.getUTCMinutes() + ':' + $scope.startDate.getUTCSeconds());
            console.log('start dec LOCALE: ' + $scope.startDate.getDate() + "/" + ($scope.startDate.getMonth()+1)  + "/" + $scope.startDate.getFullYear() + " @ "  + $scope.startDate.getHours() + ":"  + $scope.startDate.getMinutes() + ":" + $scope.startDate.getSeconds());

            $scope.startDate4 = new Date($scope.startDate.getUTCFullYear(), $scope.startDate.getUTCMonth(), $scope.startDate.getUTCDate());
            $scope.startTime4 = new Date($scope.startDate.getUTCFullYear(), $scope.startDate.getUTCMonth(), $scope.startDate.getUTCDate(), $scope.startDate.getUTCHours(), $scope.startDate.getUTCMinutes());

            //$scope.startTime4 = $scope.startDate.getUTCHours();
            //if($scope.startTime4<10){
            //  $scope.startTime4 = $scope.startTime4 +'0'+ $scope.startDate.getUTCHours();
            //}

            /*$scope.startDate = start;
             if(start.hasTime()){
             console.log('start with time');
             var startTime = start.time();
             console.log('startTime: ' + startTime);
             $scope.startdate4 = new Date(start);//.stripZone
             $scope.startTime4 = new Date(start);
             }else{
             console.log('start without time');
             $scope.startdate4 = new Date(start);
             }
             */

            $scope.endDate = new Date(end);
            console.log('end: ' + end);
            console.log('end formatted: ' + end.format("dddd, MMMM Do YYYY, h:mm:ss a"));
            console.log('end MOMENT ISO: ' + end.format('YYYY-MM-DD')+'T'+end.format('HH:mm:ss.SSS'));
            console.log('end dec UTC: ' + $scope.endDate.getUTCFullYear() + '-' + ($scope.endDate.getUTCMonth() + 1) + '-' + $scope.endDate.getUTCDate() + 'T' + $scope.endDate.getUTCHours() + ':' + $scope.endDate.getUTCMinutes() + ':' + $scope.endDate.getUTCSeconds());
            console.log('end dec LOCALE: ' + $scope.endDate.getDate() + "/" + ($scope.endDate.getMonth()+1)  + "/" + $scope.endDate.getFullYear() + " @ "  + $scope.endDate.getHours() + ":"  + $scope.endDate.getMinutes() + ":" + $scope.endDate.getSeconds());

            $scope.endDate4 = new Date($scope.endDate.getUTCFullYear(), $scope.endDate.getUTCMonth(), $scope.endDate.getUTCDate());
            $scope.endTime4 = new Date($scope.endDate.getUTCFullYear(), $scope.endDate.getUTCMonth(), $scope.endDate.getUTCDate(), $scope.endDate.getUTCHours(), $scope.endDate.getUTCMinutes());

            //$scope.endDate4 = new Date(end.stripZone());
            //$scope.endTime4 = new Date(end.stripZone());

            /*
             if(end.hasTime()){
             console.log('end with time');
             var endTime = end.time();
             console.log('endTime: ' + endTime);
             $scope.endDate4 = new Date(end);//.stripZone()
             $scope.endTime4 = new Date(end);
             }else{
             console.log('start without time');
             $scope.endDate4 = new Date(end);
             }
             */

            $scope.periodSelected = true;
            console.log('select END');
          },
          eventClick: $scope.alertOnEventClick,
          eventDrop: $scope.alertOnDrop,
          eventResize: $scope.alertOnResize,
          eventRender: $scope.eventRender,
          dayRender: function (date, cell) {
            if($scope.holidays){
              for (var i = 0; i < $scope.holidays.length; i++) {
                if (date >= $scope.holidays[i].start && date < $scope.holidays[i].end) {
                  $(cell).removeClass('ui-widget-content');
                  $(cell).addClass('holiday');
                  break;
                }
              }
            }
          }
          /*
           dayClick: function(date, jsEvent, view) {
           console.log('dayClick INIT');
           $scope.startDate = date;
           //$scope.startdate4= new Date(date);
           $scope.allDay = true;
           console.log('Selezionato giorno: ' + $scope.startDate.format("dddd, MMMM Do YYYY, h:mm:ss a"));
           //console.log('Coordinates: ' + jsEvent.pageX + ',' + jsEvent.pageY);
           //console.log('Current view: ' + view.name);

           //var moment = $('#calendar1').fullCalendar('getDate');
           //console.log('Moment on: ' + $filter('date')(moment, "dd/MM/yyyy"));

           // change the day's background color just for fun
           //$(this).css('background-color', 'red');
           $scope.periodSelected = true;
           console.log('dayClick END');
           }
           */
        }
      };

      // Create the login modal that we will use later
      $ionicModal.fromTemplateUrl('templates/event.html', {
        scope: $scope
      }).then(function(modal) {
        $scope.modalEvent = modal;
      });

      // Triggered in the login modal to close it
      $scope.closeEventModal = function() {
        $scope.modalEvent.hide();
      };

      // Open the login modal
      $scope.showEventModal = function() {
        if(!$scope.newEvent){
          $scope.newEvent = {};
        }
        if(!$scope.startDate4){
          var now = new Date();
          $scope.startDate4 = new Date(now.getUTCFullYear(), now.getUTCMonth(), now.getUTCDate());
          $scope.startTime4 = new Date(now.getUTCFullYear(), now.getUTCMonth(), now.getUTCDate(), 9, 0);
          $scope.endDate4 = new Date(now.getUTCFullYear(), now.getUTCMonth(), now.getUTCDate());
          $scope.endTime4 = new Date(now.getUTCFullYear(), now.getUTCMonth(), now.getUTCDate(), 18, 0);
        }
        $scope.showEventTitle = false;
        $scope.modalEvent.show();
      };

      $scope.changeEventCategory = function() {
        if($scope.newEvent.category == 1){
          //don sangue
          $scope.showEventTitle = false;
        }else if($scope.newEvent.category == 2){
          //Permesso
          $scope.showEventTitle = false;
        }else if($scope.newEvent.category == 3){
          //malattia
          $scope.showEventTitle = false;
        }else if($scope.newEvent.category == 4){
          //allattamento
          $scope.showEventTitle = false;
        }else if($scope.newEvent.category == 5){
          //ferie
          $scope.showEventTitle = false;
        }else if($scope.newEvent.category == 6){
          // straordinario
          $scope.showEventTitle = false;
        }else if($scope.newEvent.category == 7){
          // festa
          $scope.showEventTitle = true;
        }else if($scope.newEvent.category == 8){
          // chiusura
          $scope.showEventTitle = false;
        }
      };

      // Perform the login action when the user submits the login form
      //$scope.updateEvent = function() {
      //  console.log('Modifica evento: ', $scope.newEvent);

      // Simulate a login delay. Remove this and replace with your login
      // code if using a login system
      //  $timeout(function() {
      //    $scope.closeEventModal();
      //  }, 1000);
      //};
      //-------------------------------------------------------------

    })

.controller('BadgeCtrl', function($scope) {
  $scope.started = false;
  $scope.date = new Date();

  $scope.start = function(){
    $scope.started = true;
  };

  $scope.stop = function(){
    $scope.started = false;
  };
})

.controller('ProfilaCtrl', function($scope) {
    /*
    $scope.otherProfiles = [{profile: 'admin' },{profile: 'admin' },{profile: 'admin' }}
    ];*/


    //add to the real data holder
    $scope.addUser = function addUser() {/*
        $scope.rowCollection.push(generateRandomItem(id));
        id++;*/
    };

    //remove to the real data holder
    $scope.removeItem = function removeItem(row) {/*
        var index = $scope.rowCollection.indexOf(row);
        if (index !== -1) {
            $scope.rowCollection.splice(index, 1);
        }*/
    }

})

.controller('GestioneCtrl', function($scope) {
})

.controller('UserCtrl', function($scope, $rootScope) {

})

.controller('PasswordCtrl', function($scope, $rootScope, $ionicPopup, $timeout) {

    $scope.loginData = {};

    $scope.cambiaPassword = function() {
        var myPopup = $ionicPopup.show({
            template: '<input type="password" ng-model="loginData.newPassword2">',
            title: 'Conferma password',
            subTitle: 'Conferma nuova password',
            scope: $scope,
            buttons: [
                { text: 'Cancel' },
                {
                    text: '<b>Save</b>',
                    type: 'button-positive',
                    onTap: function(e) {
                        if($scope.loginData.newPassword1 != $scope.loginData.newPassword2) {
                            e.preventDefault();
                            console.log('Password diverse, inserire nei campi password identiche');
                           // $ionicPopup.show({ Password });
                        }
                        else {
                            //$rootScope.userLogged.password = $scope.loginData.newPassword1;
                            //console.log('Corrente',$rootScope.users[$rootScope.userLogged.id].password);
                            $rootScope.users[$rootScope.userLogged.id].password = $scope.loginData.newPassword2;
                            $rootScope.users[$rootScope.userLogged.id].password = $scope.loginData.newPassword1;
                            console.log('Cambiata', $scope.loginData.newPassword2);
                        }
                    }
                }
            ]
        });

        $timeout(function() {
            myPopup.close(); //close the popup after 10 seconds for some reason
        }, 10000);

    };

});
/* EOF */