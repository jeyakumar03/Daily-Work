package source.controller;
    public class QueriesContainer { 
    public enum Queries {
        INSERT_USER("insert into users(name,mobile,mail) values(?,?,?)"),
        INSERT_USER_CRED("insert into usercred(id,username,password) values (?,?,?)"),
        SELECT_ID("select id from users where mail=?"),
        LOGIN_QUERY("SELECT id FROM usercred WHERE username = ? AND password = ?"),
        INSERT_ADMIN("insert into admin(name,mobile,mail,travels_name) values(?,?,?,?)"),
        INSERT_ADMIN_CRED("insert into admincred(id,username,password) values(?,?,?)"),
        SELECT_ADMIN_ID("select id from admin where mail=?"),
        ADMIN_LOGIN_QUERY("SELECT id FROM admincred WHERE username = ? AND password = ?"),
        ADMIN_DELETE("delete from admin where mail=?"),
        DISPLAY_ROUTE("select * from route"),
        DISPLAY_ROUTE_TO("select * from route_toPlace"),
        DISPLAY_ADMIN("select * from admin"),
        GET_SEAT_AVAILABILITY ("SELECT seat_number FROM seats WHERE bus_id = ? AND travel_date = ?"),
        GET_BOOKED_SEATS("SELECT seat_number FROM seat_booking WHERE bus_id = ? AND travel_date = ?"),
        DISPLAY_SEAT("Select seat_capacity from bus where id=?"),
        CURRENT_SEAT_AVAILABLE("select sum(tickets_booked) FROM booking where bus_id=? and booking_date=(select current_date) group by booking_date"),
        DISPLAY_BUS("SELECT bus.id,bus.bus_number AS Bus_Number,bus.seat_capacity AS Seat,bus.isac AS AC,bus.issleeper AS Sleeper,admin.travels_name AS Travels_Name,route.from_place AS Starting_Place, route_toPlace.to_place AS Destination FROM bus JOIN route ON bus.from_place = route.id JOIN route_toPlace ON bus.to_place = route_toPlace.id JOIN admin ON bus.admin_id = admin.id WHERE bus.admin_id = ?"),
        INSERT_BUS("insert into bus(bus_number,seat_capacity,isac,issleeper,admin_id,from_place,to_place,departure_time) values(?,?,?,?,?,?,?,?)"),
        DISPLAY_AVAILABLE_BUS("SELECT bus.id, bus.bus_number, bus.seat_capacity, bus.isac, bus.issleeper, admin.travels_name, route.from_place, route_toPlace.to_place,departure_time  FROM bus JOIN route ON bus.from_place = route.id JOIN route_toPlace ON bus.to_place = route_toPlace.id JOIN admin ON bus.admin_id = admin.id WHERE bus.from_place = ? AND bus.to_place = ?"),
        AVAILABLE_SEAT("select sum(tickets_booked) FROM booking where bus_id=? and travel_date=? group by travel_date"),
        SELECT_BOOKING("SELECT  b.id AS booking_id,b.tickets_booked as Tickets_Booked,b.booked_date as Booked_Date,b.travel_date as Travel_date,bus.bus_number as Bus_Number,bus.isac as Type, bus.issleeper as Sleeper, admin.travels_name as Travels_Name,bus.departure_time as Departure_Time FROM booking b JOIN bus ON b.bus_id = bus.id JOIN  admin ON bus.admin_id = admin.id WHERE b.user_id = ?"),
        VIEW_TICKET("SELECT booking.id AS id, booking.user_id AS user_id, booking.travel_date AS travel_date, " +
                       "booking.booked_date AS booked_date, users.name AS Name, users.mobile AS mobile, " +
                       "users.mail AS Mail, booking.tickets_booked AS No_Of_Tickets " +
                       "FROM booking " +
                       "JOIN users ON booking.user_id = users.id " +
                       "WHERE booking.bus_id = ?"),
         SEAT_BOOKING("INSERT INTO seat_booking (booking_id, user_id, bus_id, seat_number, travel_date, booking_date) VALUES (?, ?, ?, ?, ?, ?)"),
        PASSENGER_QUERY("INSERT INTO passenger (booking_id, name, gender, mobile, email) VALUES (?, ?, ?, ?, ?)"),
        BOOKING_QUERY("INSERT INTO booking(user_id, bus_id, tickets_booked, booked_date, travel_date) VALUES (?, ?, ?, ?, ?)"); 
        private final String query;  
        Queries(String query) {
            this.query = query;
        }
        public String getQuery() {
            return this.query;
        }
    }

}

