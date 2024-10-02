package kr.rtuserver.lib.bukkit.core.modules;

import kr.rtuserver.lib.bukkit.api.RSPlugin;
import kr.rtuserver.lib.bukkit.api.config.RSConfiguration;
import lombok.Getter;

@Getter
public class ThemeModule extends RSConfiguration implements kr.rtuserver.lib.bukkit.api.core.modules.ThemeModule {

    private String gradientStart = "#3df559";
    private String gradientEnd = "#3db8f5";
    private String prefix = "[";
    private String suffix = "] ";
    private String systemMessage = "<gradient:" + gradientStart + ":" + gradientEnd + ">시스템 메세지</gradient>\n<gray>%servertime_yyyy-MM-dd a h:mm%</gray>";

    public ThemeModule(RSPlugin plugin) {
        super(plugin, "Modules", "Theme.yml", 1);
        setup(this);
    }

    private void init() {
        gradientStart = getString("gradient.start", gradientStart, """
                그라데이션의 시작 색상입니다""");
        gradientEnd = getString("gradient.end", gradientEnd, """
                그라데이션의 종료 색상입니다""");
        prefix = getString("plugin.prefix", prefix, """
                플러그인 이름 앞에 배치되는 문자입니다""");
        suffix = getString("plugin.suffix", suffix, """
                플러그인 이름 뒤에 배치되는 문자입니다""");
        systemMessage = getString("systemMessage", systemMessage, """
                시스템 메세제의 호버 메세지입니다""");
    }

}
