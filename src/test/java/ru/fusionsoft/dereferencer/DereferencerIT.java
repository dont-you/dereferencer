package ru.fusionsoft.dereferencer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.gitlab4j.api.GitLabApi;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.fusionsoft.dereferencer.allof.MergedFileFactory;
import ru.fusionsoft.dereferencer.core.DereferencedFileFactory;
import ru.fusionsoft.dereferencer.core.FileRegister;
import ru.fusionsoft.dereferencer.core.FileRegisterBuilder;
import ru.fusionsoft.dereferencer.core.ResourceCenter;
import ru.fusionsoft.dereferencer.core.exceptions.DereferenceException;
import ru.fusionsoft.dereferencer.core.load.BaseResourceCenter;
import ru.fusionsoft.dereferencer.core.load.DefaultLoader;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DereferencerIT {

    static Dereferencer dereferencer;
    ObjectMapper jsonMapper = new ObjectMapper();

    @BeforeClass
    public static void init() {
        dereferencer = DereferencerBuilder.builder()
                .setFileRegister(FileRegisterBuilder.builder()
                        .setDereferencedFileFactory(new MergedFileFactory())
                        .build()
                ).build();
    }

    @AfterClass
    public static void doSome() {
        dereferencer.exit();
    }

    @Test
    public void Test_simple_schema_With_cycle() throws DereferenceException, ExecutionException, InterruptedException {
//        Dereferencer dereferencer = DereferencerBuilder.builder().build();
        dereferencer.dereference(URI.create("./src/test/resources/test-schemas/schemas/cycle-schema/cycle_schema_A.json"));
        assertTrue(true);
    }

    @Test
    public void delme()
            throws IOException, ExecutionException, InterruptedException {
        GitLabApi gitLabApi = new GitLabApi("", "https://gitlab.com");
    }

    @Test
    public void debug_cases() {
    }

    @Test
    public void Test_simple_schema_With_plain_name_fragment_reference()
            throws IOException, DereferenceException, ExecutionException, InterruptedException {
//        Dereferencer dereferencer = DereferencerBuilder.builder().build();
        JsonNode actual = dereferencer.dereference(URI.create("./src/test/resources/test-schemas/schemas/basic-schemas/simple_anchor_schema.json"));
        JsonNode expected = jsonMapper.readTree(Paths
                .get("./src/test/resources/test-schemas/expected-result/dereferenced_simple_anchor_schema.json").toFile());

        assertEquals(expected, actual);
    }

    @Test
    public void Test_schema_With_relative_json_pointers()
            throws IOException, DereferenceException, ExecutionException, InterruptedException {
//        Dereferencer dereferencer = DereferencerBuilder.builder().build();
        JsonNode actual = dereferencer.dereference(URI.create("./src/test/resources/test-schemas/schemas/basic-schemas/relative_json_pointers.json"));
        JsonNode expected = jsonMapper.readTree(Paths
                .get("./src/test/resources/test-schemas/expected-result/dereferenced_relative_json_pointers.json").toFile());

        assertEquals(expected, actual);
    }

    @Test
    public void Test_schema_With_urn_references()
            throws IOException, DereferenceException, ExecutionException, InterruptedException {
//       Dereferencer dereferencer = new Dereferencer(Executors.newVirtualThreadPerTaskExecutor(), new FileRegister(new DereferencedFileFactory()));
//        JsonNode actual = dereferencer.dereference(URI.create("./src/test/resources/test-schemas/schemas/urn-resolving/test_urn_resolving.yaml"));
//        JsonNode expected = jsonMapper.readTree(Paths
//                .get("./src/test/resources/test-schemas/expected-result/dereferenced_test_urn_resolving.json").toFile());
//        assertEquals(expected, actual);
    }

    @Test
    public void fuzTest() throws DereferenceException, IOException, ExecutionException, InterruptedException {

//        Dereferencer dereferencer = DereferencerBuilder.builder().enableAllOfMerge().build();
//        Dereferencer dereferencer = DereferencerBuilder.builder().build();
        String HOME = System.getenv().get("HOME");

//        JsonNode json1 = dereferencer.dereference(URI.create(HOME+"/Schemes/schemes/fipc.yaml").normalize());
//        System.out.println(json1);

        JsonNode json2 = dereferencer.dereference(URI.create(HOME + "/Schemes/service/fipc-db-service.yaml").normalize());
        System.out.println(json2);

//        JsonNode expected = jsonMapper.readTree(Paths.get(URI.create("file://" + HOME + "/Work/fipc-it-with-merge.json").normalize()).toFile());
//        System.out.println(json2.equals(expected));

        assertEquals(jsonMapper.readTree(Paths.get(URI.create("file://" + HOME + "/Work/fipc-it-with-merge.json").normalize()).toFile()), json2);
//        assertEquals(jsonMapper.readTree(Paths.get(URI.create("file://" + HOME + "/Work/fipc-it.json").normalize()).toFile()), json2);
    }

    @Test
    public void Test_simple_merge_schema()
            throws DereferenceException, IOException, ExecutionException, InterruptedException {
//        Dereferencer dereferencer = DereferencerBuilder.builder().enableAllOfMerge().build();
        JsonNode actual = dereferencer
                .dereference(URI.create("./src/test/resources/test-schemas/schemas/basic-schemas/simple_merge_schema.json"));
        JsonNode expected = jsonMapper.readTree(
                Paths.get("./src/test/resources/test-schemas/expected-result/dereferenced_simple_merge_schema.json")
                        .toFile());

        assertEquals(expected, actual);
    }

    @Test
    public void test()
            throws IOException, DereferenceException {

    }

}
