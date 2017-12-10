import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * EventManager manages a list of events and a separate list of volunteers.
 * 
 * IMPLEMENT THE METHODS OF THIS CLASS
 * 
 * It also provides methods for adding matches between events and volunteers
 * and for displaying the events and volunteers that exist.
 * 
 * An EventManager instance manages the list of events and volunteers.
 */
public class EventManager {
	
	/** the list of events */
	private List<Event> eventList;

	/** the list of volunteers */
	private List<Volunteer> volunteerList;

	/**
	 * Constructor for an EventManager instance
	 */
	public EventManager()
	{
		this.eventList = new ArrayList<Event>();
		this.volunteerList = new ArrayList<Volunteer>();
	}
	
	/**
	 * Adds an event to the list of events or returns false if the details for the event are not valid.
	 * This maintains the event list in sorted order (sort is ascending by name only).
	 * 
	 * Tip: Collections.sort can be used after a new event is added.
	 * 
	 * The following conditions result in no event being added and false being returned
	 * <ul>
	 * <li>name is null or an empty string "".</li>
	 * <li>date is not an integer in range 1 to 30, inclusive.</li>
	 * <li>the event name already exists (duplicate event names are not allowed)</li>
	 * <li>the volunteer limit is less than one</li>
	 * </ul>
	 * 
	 * @param name the name of a new event
	 * @param dateStr the string for the date of this event
	 * @param limitStr the string for the volunteers limit in this event
	 * @return true if arguments have valid format and added event successfully, otherwise false
	 */
	public boolean addEvent(String name, String dateStr, String limitStr){
		if(name == null || name == "") return false;
		int date = 0;
		int limit = 0;
		try
		{
			date = Integer.parseInt(dateStr);
			limit = Integer.parseInt(limitStr);
		}
		catch(NumberFormatException e)
		{
			return false;
		}
		if(date < 1 || date > 30) return false;
		if(limit < 1) return false;
		
		Iterator<Event> eventItr = eventList.iterator();
		while(eventItr.hasNext())
		{
			Event check = eventItr.next();
			if(check.getName().equals(name)) return false;
		}
		
		Event add = new Event(name, date, limit);
		eventList.add(add);
		Collections.sort(eventList);
		return true;
	}
		
	/**
	 * Adds a new volunteer to the list of volunteers or returns false.
	 * Maintains the volunteer list in sorted order.  
	 * 
	 * Tip: Collections.sort can be used after a new volunteer is added.
	 * 
	 * <ul>
	 * <li>Name must not be null or empty string</li>
	 * <li>Volunteer name must not be a duplicate.</li>
	 * </ul>
	 *
	 * @param name the name of a new volunteer
	 * @param availableDatesStrAry a String array that has date strings
	 */
	public boolean addVolunteer(String name, String[] availableDatesStrAry)
	{
		if(name == null || name == "") return false;
		List<Integer> dates = new ArrayList<Integer>();
		Iterator<Volunteer> volItr = volunteerList.iterator();
		while(volItr.hasNext())
		{
			Volunteer check = volItr.next();
			if(check.getName().equals(name)) return false;
		}
		for(int b = 0; b < availableDatesStrAry.length; b++)
		{
			try
			{
				int str = Integer.parseInt(availableDatesStrAry[b]);
				dates.add(str);
			}
			catch(NumberFormatException e)
			{
				return false;
			}
		}
		Volunteer add = new Volunteer(name, dates);
		volunteerList.add(add);
		return true;
	}
	
	/** 
	 * USED ONLY IF AN EVENT NEEDS TO BE REMOVED WHILE READ FROM FILE
	 * 
	 * Iterates through the event list and remove the event if event exists. 
	 * This method must also remove all the event-volunteer matches corresponding to this event.
	 * 
	 * @param name the name of the event to be removed
	 * @return true if the event existed and removed successfully, otherwise false
	 */
	public boolean removeEvent(String name) 
	{
		if(findEvent(name) == null) return false;
		Iterator<Event> search = eventList.iterator();
		int count = 0;
		while(search.hasNext())
		{
			Event tmp = search.next();
			if(tmp.getName().equals(name))
			{
				List<GraphNode> matches = tmp.getAdjacentNodes();
				Iterator<GraphNode> mch = matches.iterator();
				while(mch.hasNext())
				{
					removeMatch(tmp.getName(), mch.next().getName());
				}
				eventList.remove(count);
				return true;
			}
			count++;
		}
		return false;
	}
	
	/**
	 * Iterates through the volunteer list and removes the volunteer if volunteer exists. 
	 * Also removes all the event-volunteer matches corresponding to this volunteer
	 * 
	 * @param name the name of the volunteer to be removed
	 * @return true if volunteer existed and removed successfully, otherwise false
	 */
	public boolean removeVolunteer(String name)
	{
		if(findVolunteer(name) == null) return false;
		Iterator<Volunteer> find = volunteerList.iterator();
		int count = 0;
		while(find.hasNext())
		{
			Volunteer temp = find.next();
			if(temp.getName().equals(name))
			{
				List<GraphNode> events = temp.getAdjacentNodes();
				Iterator<GraphNode> matcher = events.iterator();
				while(matcher.hasNext())
				{
					removeMatch(matcher.next().getName(), name);
				}
				volunteerList.remove(count);
				return true;
			}
			count++;
		}
		
		return false;
	}
	
	/**
	 * Given the event name,check if the event exists in the event list. 
	 * 
	 * @param name the name of the event to be found
	 * @return event if the event exists, otherwise null.
	 */
	public Event findEvent(String name)
	{
		Iterator<Event> eventMatch = eventList.iterator();
		while(eventMatch.hasNext())
		{
			Event check = eventMatch.next();
			if(check.getName().equals(name))
			{
				return check;
			}
		}
		return null;
	}
	
	/**
	 * Return the volunteer with the given name.
	 * 
	 * @param name the name of the volunteer
	 * @return volunteer if the volunteer exists, otherwise null.
	 */
	public Volunteer findVolunteer(String name)
	{
		Iterator<Volunteer> volMatch = volunteerList.iterator();
		while(volMatch.hasNext())
		{
			Volunteer check = volMatch.next();
			if(check.getName().equals(name))
			{
				return check;
			}
		}
		return null;
	}
	
	/**
	 * This method is used to create a match between an event and a volunteer.
	 * 
	 * <ol>
	 * <li>Find the event and the volunteer from their names.</li>
	 * <li>If either is null, return false.</li>
	 * <li>If event has not reached volunteer limit and volunteer has the event's date in its availability list, then
	 *     <ol><li>add the volunteer node to the event's adjacency list</li>
	 *     <li>add the event to the volunteer's list</li>
	 *     <li>set the availability date for the volunteer to false</li>
	 *     </ol>
	 * </li>
	 * <li>return true if all is well</li>
	 * </ol> 
	 * 
	 * @param eventName the name of an event to be matched to a volunteer
	 * @param volunteerName the name of a volunteer to be matched to a event
	 * @return true if the match is created, otherwise false.
	 */
	public boolean createMatch(String eventName, String volunteerName)
	{
		Event checkE = findEvent(eventName);
		Volunteer checkV = findVolunteer(volunteerName);
		if(checkE == null || checkV == null) return false;
		
		if(checkE.getAdjacentNodes().size() < checkE.getLimit())
		{
			if(checkV.isAvailable(checkE.getDate()))
			{
				List<GraphNode> eventVols = checkE.getAdjacentNodes();
				eventVols.add(checkV);
				List<GraphNode> volsEvents = checkV.getAdjacentNodes();
				volsEvents.add(checkE);
				checkV.setUnavailable(checkE.getDate());
				return true;
			}
		}
	
		return false;
	}
	
	/**
	 * Given the event and volunteer, remove the match between them if it exists.
	 * Return true if the match is found and removed.
	 * 
	 * If a match is found:
	 * 
	 * <ul>
	 * <li>remove the volunteer from the event's volunteer list</li>
	 * <li>remove the event from volunteer's event list</li>
	 * <li>set the event's date to available for the volunteer</li>
	 * <li>return true if all is well</li>
	 * </ul>
	 * 
	 * @param eventName the name of an event to be removed from match
	 * @param volunteer the name of a volunteer to be removed from match
	 * @return true if the match existed and removed successfully, otherwise false.
	 */
	public boolean removeMatch(String eventName, String volunteerName)
	{
		Event remE = findEvent(eventName);
		Volunteer remV = findVolunteer(volunteerName);
		if(remE.isAdjacentNode(volunteerName) && remV.isAdjacentNode(eventName))
		{
			remE.removeAdjacentNode(remV);
			remV.removeAdjacentNode(remE);
			remV.setAvailable(remE.getDate());
			return true;
		}
		return false;
	}
	
	/**
	 * This method is used to display all the events along 
	 * with corresponding matches with the volunteers.
	 * Check sample files for exact format of the display.
	 * 
	 * Utilize formats defined in the Resource class
	 * to display in correct format.
	 * 
	 * Resource.STR_ERROR_DISPLAY_EVENT_FAILED
	 * Resource.STR_DISPLAY_ALL_EVENTS_PRINT_FORMAT
	 */
	public void displayAllEvents()
	{
		if(eventList.size() == 0)
		{
			System.out.print(Resource.STR_ERROR_DISPLAY_EVENT_FAILED);
		}
		System.out.printf(Resource.STR_DISPLAY_ALL_EVENTS_PRINT_FORMAT, eventList.size());
		Iterator<Event> dispCheck = eventList.iterator();
		while(dispCheck.hasNext())
		{
			Event toDisp = dispCheck.next();
			System.out.printf(Resource.STR_DISPLAY_EVENT_PRINT_FORMAT, toDisp.getName(), toDisp.getDate(), toDisp.getLimit());
			List<GraphNode> vols = toDisp.getAdjacentNodes();
			Iterator<GraphNode> stepper = vols.iterator();
			int count = 1;
			if(stepper.hasNext())
			{
				while(stepper.hasNext())
				{
					GraphNode temp = stepper.next();
					System.out.printf(Resource.STR_DISPLAY_EVENT_VOLUNTEERS_PRINT_FORMAT, count, temp.getName());
					count++;
				}
			}
			else
			{
				System.out.print(Resource.STR_DISPLAY_NO_MATCHES);
			}
			
		}
	}
	
	/**	 
	 * This method is used to display all the volunteers along 
	 * with corresponding matches with the events.
	 * Check sample files for exact format of the display.
	 * 
	 * Utilize formats defined in the resource file for 
	 * display in the correct format.
	 * 
	 * Resource.STR_ERROR_DISPLAY_VOLUNTEER_FAILED
	 * Resource.STR_DISPLAY_ALL_VOLUNTEERS_PRINT_FORMAT
	 */
	public void displayAllVolunteers()
	{
		if(volunteerList.size() == 0)
		{
			System.out.print(Resource.STR_ERROR_DISPLAY_VOLUNTEER_FAILED);
		}
		System.out.printf(Resource.STR_DISPLAY_ALL_VOLUNTEERS_PRINT_FORMAT, volunteerList.size());
		Iterator<Volunteer> volItr = volunteerList.iterator();
		while(volItr.hasNext())
		{
			Volunteer toDisp = volItr.next();
			String str = "";
			for(int i = 1; i < 31; i++)
			{
				if(toDisp.isAvailable(i))
				{
					str = str + i + ",";
				}
			}
			str = str.substring(0, str.length()-1);
			System.out.printf(Resource.STR_VOLUNTEER_PRINT_FORMAT, toDisp.getName(), str);
			
			List<GraphNode> events = toDisp.getAdjacentNodes();
			Iterator<GraphNode> eveItr = events.iterator();
			if(eveItr.hasNext())
			{
				while(eveItr.hasNext())
				{
					GraphNode hold = eveItr.next();
					Event hold2 = findEvent(hold.getName());
					System.out.printf(Resource.STR_VOLUNTEER_EVENT_PRINT_FORMAT, hold2.getName(), hold2.getDate());
				}
			}
			else
			{
				System.out.print(Resource.STR_DISPLAY_NO_MATCHES);
			}
		}
	}
	
	/**
	 * This is helper method to create a string for
	 * writing all the volunteers in a file.
	 * 
	 * (Example)
	 * <pre>
	 * v;Mingi;5,23,30
	 * v;Sonu;1,2,3,4,5
	 * </pre>
	 * 
	 * @return a single string object containing all the volunteers 
	 * in the format needed to be printed in the file.
	 */
	public String toStringAllVolunteers()
	{
		String allVols = "";
		for(Volunteer check : volunteerList)
		{
			allVols = allVols + "v;" + check.getName() + ";";
			for(int v = 1; v <= 30; v++)
			{
				if(check.isAvailable(v))
				{
					allVols = allVols + v + ",";
				}
			}
			Character lastChar = allVols.charAt(allVols.length() - 1); // should we always just trim last , ?
			if(lastChar.equals(','))
			{
				allVols = allVols.substring(0, allVols.length() - 1);
			}
			allVols = allVols + "\n";
		}
		return allVols.trim();
	}
	
	/**
	 * This is helper method to create a string for
	 * writing all the events in a file.
	 * 
	 * (Example)
	 * e;Field trip;7;10;
	 * e;Birthday;23;12;Mingi,Sonu
	 * 
	 * @return string containing all the events in the format
	 * needed to be printed in the file.
	 */
	public String toStringAllEvents()
	{
		String allEvents = "";
		for(Event check : eventList)
		{
			allEvents = allEvents + "e;" + check.getName() + ";" + check.getDate() + ";" + check.getLimit() + ";";
			List<GraphNode> volunteers = check.getAdjacentNodes();
			Iterator<GraphNode> itr = volunteers.iterator();
			while(itr.hasNext())
			{
				GraphNode vol = itr.next();
				allEvents = allEvents + vol.getName() + ";";
			}
			allEvents = allEvents + "\n";
		}
		return allEvents.trim();
	}
<<<<<<< HEAD
}
=======
}

>>>>>>> c234261376e93a3fe7dc2890aef8d7db5ff686e7
