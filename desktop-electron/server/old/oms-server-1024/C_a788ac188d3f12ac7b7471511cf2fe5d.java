import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_a788ac188d3f12ac7b7471511cf2fe5d
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        return CommonFunc.getMapValueByKey("万江巡维;盛丰巡维;大朗巡维;跃立巡维;板桥巡维;立新巡维;松山湖巡维;长安巡维;景湖巡维;北栅巡维;掌洲巡维;南粤巡维;水乡巡维;莞城巡维;横沥巡维;葵湖巡维;寒溪巡维;元江巡维;步田巡维;东莞巡维;莆心巡维;裕元巡维;",mapRow.get("XWZX"));

    }
}
