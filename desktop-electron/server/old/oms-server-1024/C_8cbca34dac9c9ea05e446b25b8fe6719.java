import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_8cbca34dac9c9ea05e446b25b8fe6719
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        return CommonFunc.getMapValueByKey("万江巡维中心;寒溪巡维中心;大朗巡维中心;跃立巡维中心;板桥巡维中心;立新巡维中心;松山湖巡维中心;长安巡维中心;景湖巡维中心;北栅巡维中心;掌洲巡维中心;南粤巡维中心;水乡巡维中心;莞城巡维中心;横沥巡维中心;葵湖巡维中心;元江巡维中心;步田巡维中心;东莞巡维中心;莆心巡维中心;裕元巡维中心;",mapRow.get("DEPARTMENTS"));

    }
}
