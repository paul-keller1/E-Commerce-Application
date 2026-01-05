package com.app.service;

import com.app.benchmark.AbstractBenchmark;
import com.app.dto.AddressDTO;
import com.app.model.Address;
import com.app.repository.AddressRepo;
import com.app.repository.UserRepo;
import com.app.service.AddressService;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class AddressServiceImplBenchmark extends AbstractBenchmark {

    // Spring beans stored in statics (needed because forks(0) + JUnit bootstrap pattern)
    private static AddressService addressService;
    private static AddressRepo addressRepo;
    private static UserRepo userRepo;

    @Autowired
    void setBeans(AddressService svc, AddressRepo aRepo, UserRepo uRepo) {
        AddressServiceImplBenchmark.addressService = svc;
        AddressServiceImplBenchmark.addressRepo = aRepo;
        AddressServiceImplBenchmark.userRepo = uRepo;
    }

    private Long existingAddressId;

    // Used for delete/update benchmarks to have fresh rows per invocation
    private Long invocationAddressId;

    // Controls data size for getAddresses() / test size
    @Param({"100"})
    public int addressCount;

    @Setup(Level.Trial)
    public void trialSetup() {
        // Clean DB (order matters if FK constraints exist)
        userRepo.deleteAll();
        addressRepo.deleteAll();

        // Seed addresses
        for (int i = 0; i < addressCount; i++) {
            addressRepo.save(makeAddressEntity("DE", "BE", "Berlin", "10115", "Street " + i, "Building " + i));
        }

        existingAddressId = addressRepo.findAll()
                .stream()
                .findAny()
                .orElseThrow()
                .getAddressId();
    }

    @Setup(Level.Invocation)
    public void invocationSetup() {
        // Create an address safe to update/delete each invocation
        Address a = addressRepo.save(
                makeAddressEntity(
                        "DE", "BY", "Munich", "80331",
                        "Inv-Street-" + UUID.randomUUID(),
                        "Inv-Building-" + UUID.randomUUID()
                )
        );
        invocationAddressId = a.getAddressId();
    }

    // ---------------- Benchmarks ----------------

    @Benchmark
    public void createAddress_unique(Blackhole bh) {
        AddressDTO dto = new AddressDTO();
        dto.setCountry("DE");
        dto.setState("BW");
        dto.setCity("Stuttgart");
        dto.setPincode("70173");
        dto.setStreet("Create-" + UUID.randomUUID());
        dto.setBuildingName("B-" + UUID.randomUUID());

        bh.consume(addressService.createAddress(dto));
    }

    @Benchmark
    public void getAddress_byId(Blackhole bh) {
        bh.consume(addressService.getAddress(existingAddressId));
    }

    @Benchmark
    public void getAddresses_all(Blackhole bh) {
        List<?> res = addressService.getAddresses();
        bh.consume(res);
    }

    @Benchmark
    public void updateAddress_noDuplicatePath(Blackhole bh) {
        Address update = new Address();
        update.setCountry("DE");
        update.setState("HH");
        update.setCity("Hamburg");
        update.setPincode("20095");
        update.setStreet("Upd-" + UUID.randomUUID());
        update.setBuildingName("Upd-B-" + UUID.randomUUID());

        bh.consume(addressService.updateAddress(invocationAddressId, update));
    }

    @Benchmark
    public void deleteAddress_existing(Blackhole bh) {
        bh.consume(addressService.deleteAddress(invocationAddressId));
    }

    @Benchmark
    public void updateAddress_duplicateMergePath(Blackhole bh) {
        Address canonical = ensureCanonicalAddress();

        Address update = new Address();
        update.setCountry(canonical.getCountry());
        update.setState(canonical.getState());
        update.setCity(canonical.getCity());
        update.setPincode(canonical.getPincode());
        update.setStreet(canonical.getStreet());
        update.setBuildingName(canonical.getBuildingName());

        bh.consume(addressService.updateAddress(invocationAddressId, update));
    }

    // ---------------- Helpers ----------------

    private Address ensureCanonicalAddress() {
        String country = "DE";
        String state = "NW";
        String city = "Cologne";
        String pincode = "50667";
        String street = "Domkloster";
        String building = "Cathedral";

        Address fromDb = addressRepo.findByCountryAndStateAndCityAndPincodeAndStreetAndBuildingName(
                country, state, city, pincode, street, building
        );
        if (fromDb != null) return fromDb;

        return addressRepo.save(makeAddressEntity(country, state, city, pincode, street, building));
    }

    private static Address makeAddressEntity(
            String country, String state, String city,
            String pincode, String street, String buildingName
    ) {
        Address a = new Address();
        a.setCountry(country);
        a.setState(state);
        a.setCity(city);
        a.setPincode(pincode);
        a.setStreet(street);
        a.setBuildingName(buildingName);
        return a;
    }
}
