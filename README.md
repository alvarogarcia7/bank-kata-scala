## Unresolved questions


  - How to model the actors vs the bank accounts? One per account?
  - Where is the persistence really handled?
  - How to stop the user sending sensitive messages:
    - E.g., Out Of order to an actor
    - E.g., PIN is very sensitive. Is there an SSL equivalent?
  - How to handle the borders/frontier of my actor system. When connecting the actor to the Printer, how to do it? Now, it's using a "Success printing" event, to help with the synchronization
