## Auth Service
**Authentication provider, jwt mint, and profile service.** 

Will provide: 
1. User Registration.
2. Login Service.
3. JWT mint.
4. Profile data.

### Notes: 
1. Manually implemented Spring BCrypt because GraalVM did not support apache commons logging's reflection.  
   * Switched out for Micronaut's standard logback.
2. Migrated to R2DBC for reactive crud repository.