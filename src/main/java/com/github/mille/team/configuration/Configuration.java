package com.github.mille.team.configuration;

import com.github.mille.team.Server;
import com.github.mille.team.scheduler.FileWriteTask;
import com.github.mille.team.utils.MainLogger;
import com.github.mille.team.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * author: MagicDroidX Nukkit
 */
public class Configuration {

    public static final int DETECT = -1; //Detect by file extension

    public static final int PROPERTIES = 0; // .properties

    public static final int CNF = Configuration.PROPERTIES; // .cnf

    public static final int JSON = 1; // .js, .json

    public static final int YAML = 2; // .yml, .yaml

    //public static final int EXPORT = 3; // .export, .xport
    //public static final int SERIALIZED = 4; // .sl
    public static final int ENUM = 5; // .txt, .list, .enum

    public static final int ENUMERATION = Configuration.ENUM;

    public static final Map<String, Integer> format = new TreeMap<>();

    static {
        format.put("properties", Configuration.PROPERTIES);
        format.put("con", Configuration.PROPERTIES);
        format.put("conf", Configuration.PROPERTIES);
        format.put("config", Configuration.PROPERTIES);
        format.put("js", Configuration.JSON);
        format.put("json", Configuration.JSON);
        format.put("yml", Configuration.YAML);
        format.put("yaml", Configuration.YAML);
        //format.put("sl", Config.SERIALIZED);
        //format.put("serialize", Config.SERIALIZED);
        format.put("txt", Configuration.ENUM);
        format.put("list", Configuration.ENUM);
        format.put("enum", Configuration.ENUM);
    }

    private final Map<String, Object> nestedCache = new HashMap<>();

    //private LinkedHashMap<String, Object> config = new LinkedHashMap<>();
    private ConfigurationSection config = new ConfigurationSection();

    private File file;

    private boolean correct = false;

    private int type = Configuration.DETECT;

    /**
     * Constructor for Config instance with undefined file object
     *
     * @param type - Config type
     */
    public Configuration(int type) {
        this.type = type;
        this.correct = true;
        this.config = new ConfigurationSection();
    }

    /**
     * Constructor for Config (YAML) instance with undefined file object
     */
    public Configuration() {
        this(Configuration.YAML);
    }

    public Configuration(String file) {
        this(file, Configuration.DETECT);
    }

    public Configuration(File file) {
        this(file.toString(), Configuration.DETECT);
    }

    public Configuration(
        String file,
        int type
    ) {
        this(file, type, new ConfigurationSection());
    }

    public Configuration(
        File file,
        int type
    ) {
        this(file.toString(), type, new ConfigurationSection());
    }

    @Deprecated
    public Configuration(
        String file,
        int type,
        LinkedHashMap<String, Object> defaultMap
    ) {
        this.load(file, type, new ConfigurationSection(defaultMap));
    }

    public Configuration(
        String file,
        int type,
        ConfigurationSection defaultMap
    ) {
        this.load(file, type, defaultMap);
    }

    public Configuration(
        File file,
        int type,
        ConfigurationSection defaultMap
    ) {
        this.load(file.toString(), type, defaultMap);
    }

    @Deprecated
    public Configuration(
        File file,
        int type,
        LinkedHashMap<String, Object> defaultMap
    ) {
        this(file.toString(), type, new ConfigurationSection(defaultMap));
    }

    public void reload() {
        this.config.clear();
        this.nestedCache.clear();
        this.correct = false;
        //this.load(this.file.toString());
        if (this.file == null) throw new IllegalStateException("Failed to reload Config. File object is undefined.");
        this.load(this.file.toString(), this.type);

    }

    public boolean load(String file) {
        return this.load(file, Configuration.DETECT);
    }

    public boolean load(
        String file,
        int type
    ) {
        return this.load(file, type, new ConfigurationSection());
    }

    @SuppressWarnings("unchecked")
    public boolean load(
        String file,
        int type,
        ConfigurationSection defaultMap
    ) {
        this.correct = true;
        this.type = type;
        this.file = new File(file);
        if (!this.file.exists()) {
            try {
                this.file.getParentFile().mkdirs();
                this.file.createNewFile();
            } catch (IOException e) {
                MainLogger.getLogger().error("Could not create Config " + this.file, e);
            }
            this.config = defaultMap;
            this.save();
        } else {
            if (this.type == Configuration.DETECT) {
                String extension = "";
                if (this.file.getName().lastIndexOf(".") != -1 && this.file.getName().lastIndexOf(".") != 0) {
                    extension = this.file.getName().substring(this.file.getName().lastIndexOf(".") + 1);
                }
                if (format.containsKey(extension)) {
                    this.type = format.get(extension);
                } else {
                    this.correct = false;
                }
            }
            if (this.correct) {
                String content = "";
                try {
                    content = Utils.readFile(this.file);
                } catch (IOException e) {
                    Server.getInstance().getLogger().logException(e);
                }
                this.parseContent(content);
                if (!this.correct) return false;
                if (this.setDefault(defaultMap) > 0) {
                    this.save();
                }
            } else {
                return false;
            }
        }
        return true;
    }

    public boolean load(InputStream inputStream) {
        if (inputStream == null) return false;
        if (this.correct) {
            String content;
            try {
                content = Utils.readFile(inputStream);
            } catch (IOException e) {
                Server.getInstance().getLogger().logException(e);
                return false;
            }
            this.parseContent(content);
        }
        return correct;
    }

    public boolean check() {
        return this.correct;
    }

    public boolean isCorrect() {
        return correct;
    }

    /**
     * Save configuration into provided file. Internal file object will be set to new file.
     *
     * @param file
     * @param async
     * @return
     */
    public boolean save(
        File file,
        boolean async
    ) {
        this.file = file;
        return save(async);
    }

    public boolean save(File file) {
        this.file = file;
        return save();
    }

    public boolean save() {
        return this.save(false);
    }

    public boolean save(Boolean async) {
        if (this.file == null) throw new IllegalStateException("Failed to save Config. File object is undefined.");
        if (this.correct) {
            String content = "";
            switch (this.type) {
                case Configuration.PROPERTIES:
                    content = this.writeProperties();
                    break;
                case Configuration.JSON:
                    content = new GsonBuilder().setPrettyPrinting().create().toJson(this.config);
                    break;
                case Configuration.YAML:
                    DumperOptions dumperOptions = new DumperOptions();
                    dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
                    Yaml yaml = new Yaml(dumperOptions);
                    content = yaml.dump(this.config);
                    break;
                case Configuration.ENUM:
                    for (Object o : this.config.entrySet()) {
                        Map.Entry entry = (Map.Entry) o;
                        content += entry.getKey() + "\r\n";
                    }
                    break;
            }
            if (async) {
                Server.getInstance().getScheduler().scheduleAsyncTask(new FileWriteTask(this.file, content));

            } else {
                try {
                    Utils.writeFile(this.file, content);
                } catch (IOException e) {
                    Server.getInstance().getLogger().logException(e);
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public void set(
        final String key,
        Object value
    ) {
        this.config.set(key, value);
    }

    public Object get(String key) {
        return this.get(key, null);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(
        String key,
        T defaultValue
    ) {
        return this.correct ? this.config.get(key, defaultValue) : defaultValue;
    }

    public ConfigurationSection getSection(String key) {
        return this.correct ? this.config.getSection(key) : new ConfigurationSection();
    }

    public boolean isSection(String key) {
        return config.isSection(key);
    }

    public ConfigurationSection getSections(String key) {
        return this.correct ? this.config.getSections(key) : new ConfigurationSection();
    }

    public ConfigurationSection getSections() {
        return this.correct ? this.config.getSections() : new ConfigurationSection();
    }

    public int getInt(String key) {
        return this.getInt(key, 0);
    }

    public int getInt(
        String key,
        int defaultValue
    ) {
        return this.correct ? this.config.getInt(key, defaultValue) : defaultValue;
    }

    public boolean isInt(String key) {
        return config.isInt(key);
    }

    public long getLong(String key) {
        return this.getLong(key, 0);
    }

    public long getLong(
        String key,
        long defaultValue
    ) {
        return this.correct ? this.config.getLong(key, defaultValue) : defaultValue;
    }

    public boolean isLong(String key) {
        return config.isLong(key);
    }

    public double getDouble(String key) {
        return this.getDouble(key, 0);
    }

    public double getDouble(
        String key,
        double defaultValue
    ) {
        return this.correct ? this.config.getDouble(key, defaultValue) : defaultValue;
    }

    public boolean isDouble(String key) {
        return config.isDouble(key);
    }

    public String getString(String key) {
        return this.getString(key, "");
    }

    public String getString(
        String key,
        String defaultValue
    ) {
        return this.correct ? this.config.getString(key, defaultValue) : defaultValue;
    }

    public boolean isString(String key) {
        return config.isString(key);
    }

    public boolean getBoolean(String key) {
        return this.getBoolean(key, false);
    }

    public boolean getBoolean(
        String key,
        boolean defaultValue
    ) {
        return this.correct ? this.config.getBoolean(key, defaultValue) : defaultValue;
    }

    public boolean isBoolean(String key) {
        return config.isBoolean(key);
    }

    public List getList(String key) {
        return this.getList(key, null);
    }

    public List getList(
        String key,
        List defaultList
    ) {
        return this.correct ? this.config.getList(key, defaultList) : defaultList;
    }

    public boolean isList(String key) {
        return config.isList(key);
    }

    public List<String> getStringList(String key) {
        return config.getStringList(key);
    }

    public List<Integer> getIntegerList(String key) {
        return config.getIntegerList(key);
    }

    public List<Boolean> getBooleanList(String key) {
        return config.getBooleanList(key);
    }

    public List<Double> getDoubleList(String key) {
        return config.getDoubleList(key);
    }

    public List<Float> getFloatList(String key) {
        return config.getFloatList(key);
    }

    public List<Long> getLongList(String key) {
        return config.getLongList(key);
    }

    public List<Byte> getByteList(String key) {
        return config.getByteList(key);
    }

    public List<Character> getCharacterList(String key) {
        return config.getCharacterList(key);
    }

    public List<Short> getShortList(String key) {
        return config.getShortList(key);
    }

    public List<Map> getMapList(String key) {
        return config.getMapList(key);
    }

    public boolean exists(String key) {
        return config.exists(key);
    }

    public boolean exists(
        String key,
        boolean ignoreCase
    ) {
        return config.exists(key, ignoreCase);
    }

    public void remove(String key) {
        config.remove(key);
    }

    public Map<String, Object> getAll() {
        return this.config.getAllMap();
    }

    public void setAll(LinkedHashMap<String, Object> map) {
        this.config = new ConfigurationSection(map);
    }

    public void setAll(ConfigurationSection section) {
        this.config = section;
    }

    /**
     * Get root (main) config section of the Config
     *
     * @return
     */
    public ConfigurationSection getRootSection() {
        return config;
    }

    public int setDefault(LinkedHashMap<String, Object> map) {
        return setDefault(new ConfigurationSection(map));
    }

    public int setDefault(ConfigurationSection map) {
        int size = this.config.size();
        this.config = this.fillDefaults(map, this.config);
        return this.config.size() - size;
    }


    private ConfigurationSection fillDefaults(
        ConfigurationSection defaultMap,
        ConfigurationSection data
    ) {
        for (String key : defaultMap.keySet()) {
            if (!data.containsKey(key)) {
                data.put(key, defaultMap.get(key));
            }
        }
        return data;
    }

    private void parseList(String content) {
        content = content.replace("\r\n", "\n");
        for (String v : content.split("\n")) {
            if (v.trim().isEmpty()) {
                continue;
            }
            config.put(v, true);
        }
    }

    private String writeProperties() {
        String content = "#Properties Config file\r\n#" + new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()) + "\r\n";
        for (Object o : this.config.entrySet()) {
            Map.Entry entry = (Map.Entry) o;
            Object v = entry.getValue();
            Object k = entry.getKey();
            if (v instanceof Boolean) {
                v = (Boolean) v ? "on" : "off";
            }
            content += k + "=" + v + "\r\n";
        }
        return content;
    }

    private void parseProperties(String content) {
        for (String line : content.split("\n")) {
            if (Pattern.compile("[a-zA-Z0-9\\-_\\.]*+=+[^\\r\\n]*").matcher(line).matches()) {
                String[] b = line.split("=", -1);
                String k = b[0];
                String v = b[1].trim();
                String v_lower = v.toLowerCase();
                if (this.config.containsKey(k)) {
                    MainLogger.getLogger().debug("[Config] Repeated property " + k + " on file " + this.file.toString());
                }
                switch (v_lower) {
                    case "on":
                    case "true":
                    case "yes":
                        this.config.put(k, true);
                        break;
                    case "off":
                    case "false":
                    case "no":
                        this.config.put(k, false);
                        break;
                    default:
                        this.config.put(k, v);
                        break;
                }
            }
        }
    }

    /**
     * @deprecated use {@link #get(String)} instead
     */
    @Deprecated
    public Object getNested(String key) {
        return get(key);
    }

    /**
     * @deprecated use {@link #get(String, T)} instead
     */
    @Deprecated
    public <T> T getNested(
        String key,
        T defaultValue
    ) {
        return get(key, defaultValue);
    }

    /**
     * @deprecated use {@link #get(String)} instead
     */
    @Deprecated
    @SuppressWarnings("unchecked")
    public <T> T getNestedAs(
        String key,
        Class<T> type
    ) {
        return (T) get(key);
    }

    /**
     * @deprecated use {@link #remove(String)} instead
     */
    @Deprecated
    public void removeNested(String key) {
        remove(key);
    }

    private void parseContent(String content) {
        switch (this.type) {
            case Configuration.PROPERTIES:
                this.parseProperties(content);
                break;
            case Configuration.JSON:
                GsonBuilder builder = new GsonBuilder();
                Gson gson = builder.create();
                this.config = new ConfigurationSection(gson.fromJson(content, new TypeToken<LinkedHashMap<String, Object>>() {

                }.getType()));
                break;
            case Configuration.YAML:
                DumperOptions dumperOptions = new DumperOptions();
                dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
                Yaml yaml = new Yaml(dumperOptions);
                this.config = new ConfigurationSection(yaml.loadAs(content, LinkedHashMap.class));
                if (this.config == null) {
                    this.config = new ConfigurationSection();
                }
                break;
            // case Config.SERIALIZED
            case Configuration.ENUM:
                this.parseList(content);
                break;
            default:
                this.correct = false;
        }
    }

    public Set<String> getKeys() {
        if (this.correct) return config.getKeys();
        return new HashSet<>();
    }

    public Set<String> getKeys(boolean child) {
        if (this.correct) return config.getKeys(child);
        return new HashSet<>();
    }

}
