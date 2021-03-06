package gordon.study.cache.springcache.multicache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import gordon.study.cache.ehcache3.basic.UserModel;

@Service
@CacheConfig(cacheNames = "userCache")
public class MultiCacheUserService {

    @Autowired
    MultiCacheUserRepository repo;

    @Caching(cacheable = { @Cacheable(key = "'i'+#id") }, put = {
            @CachePut(key = "'p'+#result?.phone", condition = "#root.caches[0].get('i'+#id) == null", unless = "#result == null or #result.phone == null"),
            @CachePut(key = "'e'+#result?.email", condition = "#root.caches[0].get('i'+#id) == null", unless = "#result == null or #result.email == null") })
    public UserModel findUser(String id) {
        return repo.findUser(id);
    }

    @Caching(cacheable = { @Cacheable(key = "'p'+#phone") }, put = {
            @CachePut(key = "'i'+#result?.id", condition = "#root.caches[0].get('p'+#phone) == null", unless = "#result == null"),
            @CachePut(key = "'e'+#result?.email", condition = "#root.caches[0].get('p'+#phone) == null", unless = "#result == null or #result.email == null") })
    public UserModel findUserByPhone(String phone) {
        return repo.findUserByPhone(phone);
    }

    @Caching(cacheable = { @Cacheable(key = "'e'+#email") }, put = {
            @CachePut(key = "'i'+#result?.id", condition = "#root.caches[0].get('e'+#email) == null", unless = "#result == null"),
            @CachePut(key = "'p'+#result?.phone", condition = "#root.caches[0].get('e'+#email) == null", unless = "#result == null or #result.phone == null") })
    public UserModel findUserByEmail(String email) {
        return repo.findUserByEmail(email);
    }

    @Caching(put = { @CachePut(key = "'i'+#id", unless = "#result == null"),
            @CachePut(key = "'p'+#result?.phone", unless = "#result == null or #result.phone == null"),
            @CachePut(key = "'e'+#result?.email", unless = "#result == null or #result.email == null") })
    public UserModel updateUser(String id, String info) {
        return repo.updateUser(id, info);
    }

    @Caching(evict = { @CacheEvict(key = "'p'+#result?.phone"), @CacheEvict(key = "'e'+#result?.email"), @CacheEvict(key = "'i'+#id") })
    public UserModel deleteUser(String id) {
        return repo.deleteUser(id);
    }

    @Caching(evict = {
            @CacheEvict(key = "'p'+#root.caches[0].get('i'+#id).get().phone", condition = "#root.caches[0].get('i'+#id) != null"),
            @CacheEvict(key = "'e'+#root.caches[0].get('i'+#id).get().email", condition = "#root.caches[0].get('i'+#id) != null"),
            @CacheEvict(key = "'i'+#id") })
    public boolean changePhone(String id, String newPhone) {
        return repo.changePhone(id, newPhone);
    }
}
